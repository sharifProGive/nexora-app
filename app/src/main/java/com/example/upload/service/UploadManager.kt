package com.example.upload.service

import android.content.Context
import com.example.data.database.NexoraDatabase
import com.example.data.model.CreatorContentEntity
import com.example.upload.model.UploadSettings
import com.example.upload.model.UploadStatus
import com.example.upload.model.UploadTargetType
import com.example.upload.model.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class UploadManager private constructor(private val appContext: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val activeJobs = mutableMapOf<String, Job>()

    private val _uploads = MutableStateFlow<List<UploadTask>>(emptyList())
    val uploads: StateFlow<List<UploadTask>> = _uploads.asStateFlow()
    val allTasks: StateFlow<List<UploadTask>> = _uploads.asStateFlow()

    val activeTasks: StateFlow<List<UploadTask>> = _uploads.map { list ->
        list.filter { it.status == UploadStatus.QUEUED || it.status == UploadStatus.UPLOADING || it.status == UploadStatus.PROCESSING }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val completedTasks: StateFlow<List<UploadTask>> = _uploads.map { list ->
        list.filter { it.status == UploadStatus.COMPLETED }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val failedTasks: StateFlow<List<UploadTask>> = _uploads.map { list ->
        list.filter { it.status == UploadStatus.FAILED || it.status == UploadStatus.CANCELLED }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val _settings = MutableStateFlow(UploadSettings())
    val settings: StateFlow<UploadSettings> = _settings.asStateFlow()

    private val database = NexoraDatabase.getInstance(appContext)

    companion object {
        @Volatile
        private var INSTANCE: UploadManager? = null

        fun getInstance(context: Context): UploadManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UploadManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun enqueueUpload(task: UploadTask): String {
        val finalTask = task.copy(
            status = UploadStatus.QUEUED,
            progressPercent = 0,
            uploadSpeed = "Connecting...",
            currentSpeed = "Connecting...",
            createdAt = System.currentTimeMillis()
        )
        _uploads.update { listOf(finalTask) + it }

        val job = scope.launch {
            processUpload(finalTask)
        }
        activeJobs[finalTask.id] = job
        return finalTask.id
    }

    fun enqueueUpload(
        targetType: UploadTargetType,
        title: String,
        description: String = "",
        fileUri: String,
        fileName: String,
        fileSizeFormatted: String,
        mediaType: String,
        durationFormatted: String? = null,
        resolutionFormatted: String? = null,
        thumbnailUri: String? = null,
        category: String = _settings.value.defaultCategory,
        visibility: String = _settings.value.defaultVisibility.uppercase(),
        audience: String = if (_settings.value.defaultAudience.contains("Not", ignoreCase = true)) "NOT_FOR_KIDS" else "MADE_FOR_KIDS",
        userId: String = "",
        channelId: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        val item = UploadTask(
            id = id,
            targetType = targetType,
            title = title.ifBlank { fileName },
            description = description,
            mediaUri = fileUri,
            fileUri = fileUri,
            fileName = fileName,
            fileSizeFormatted = fileSizeFormatted,
            mimeType = if (mediaType.startsWith("video", ignoreCase = true)) "video/mp4" else "image/jpeg",
            mediaType = mediaType,
            durationFormatted = durationFormatted,
            resolutionFormatted = resolutionFormatted,
            thumbnailUri = thumbnailUri,
            category = category,
            visibility = visibility,
            audience = audience,
            status = UploadStatus.QUEUED,
            progressPercent = 0,
            uploadSpeed = "Connecting...",
            currentSpeed = "Connecting...",
            createdAt = System.currentTimeMillis(),
            startedAt = System.currentTimeMillis(),
            userId = userId,
            channelId = channelId
        )

        return enqueueUpload(item)
    }

    private suspend fun processUpload(initialItem: UploadTask) {
        val id = initialItem.id

        // 1. Queue -> Uploading
        updateItem(id) {
            it.copy(status = UploadStatus.UPLOADING, currentSpeed = "2.8 MB/s", uploadSpeed = "2.8 MB/s", progressPercent = 5)
        }
        delay(350)

        // 2. Realistic incremental progress
        val progressSteps = listOf(
            15 to "3.4 MB/s",
            28 to "4.1 MB/s",
            42 to "4.8 MB/s",
            58 to "5.2 MB/s",
            73 to "4.6 MB/s",
            87 to "3.9 MB/s",
            95 to "4.2 MB/s",
            100 to "Finishing upload..."
        )

        for ((pct, speed) in progressSteps) {
            val current = _uploads.value.find { it.id == id }
            if (current == null || current.status == UploadStatus.CANCELLED) {
                return
            }
            updateItem(id) {
                it.copy(progressPercent = pct, currentSpeed = speed, uploadSpeed = speed)
            }
            delay(400)
        }

        // 3. Processing State
        updateItem(id) {
            it.copy(
                status = UploadStatus.PROCESSING,
                currentSpeed = "Processing & Transcoding...",
                uploadSpeed = "Processing...",
                progressPercent = 100
            )
        }
        delay(1800)

        val itemBeforeComplete = _uploads.value.find { it.id == id }
        if (itemBeforeComplete == null || itemBeforeComplete.status == UploadStatus.CANCELLED) {
            return
        }

        // 4. Completed & Synchronize to App Entities
        updateItem(id) {
            it.copy(
                status = UploadStatus.COMPLETED,
                completedAt = System.currentTimeMillis(),
                currentSpeed = "Completed",
                uploadSpeed = "Completed"
            )
        }

        syncCompletedUploadToDatabase(initialItem)
        activeJobs.remove(id)
    }

    private suspend fun syncCompletedUploadToDatabase(item: UploadTask) {
        try {
            when (item.targetType) {
                UploadTargetType.PROFILE_PICTURE -> {
                    if (item.userId.isNotBlank()) {
                        database.userDao().updateProfilePicture(item.userId, item.fileUri)
                    }
                }
                UploadTargetType.CHANNEL_PICTURE -> {
                    val channelId = item.channelId ?: item.userId
                    if (channelId.isNotBlank()) {
                        val current = database.channelDao().getChannelById(channelId)
                        if (current != null) {
                            database.channelDao().updateChannel(current.copy(profilePictureUri = item.fileUri))
                        }
                    }
                }
                UploadTargetType.CHANNEL_BANNER -> {
                    val channelId = item.channelId ?: item.userId
                    if (channelId.isNotBlank()) {
                        val current = database.channelDao().getChannelById(channelId)
                        if (current != null) {
                            database.channelDao().updateChannel(current.copy(bannerUri = item.fileUri))
                        }
                    }
                }
                UploadTargetType.LONG_VIDEO -> {
                    val content = CreatorContentEntity(
                        id = item.id,
                        channelId = item.channelId ?: item.userId,
                        channelName = item.channelName ?: "nexora Channel",
                        channelHandle = "",
                        userId = item.userId,
                        contentType = "VIDEO",
                        title = item.title,
                        description = item.description,
                        videoPath = item.fileUri,
                        thumbnailUri = item.thumbnailUri ?: "thumb_landscape_0.jpg",
                        visibility = item.visibility,
                        isMadeForKids = item.audience == "MADE_FOR_KIDS",
                        category = item.category,
                        durationSeconds = 45,
                        status = "PUBLISHED",
                        uploadProgress = 100,
                        createdAt = System.currentTimeMillis()
                    )
                    database.creatorContentDao().insertContent(content)
                }
                UploadTargetType.SHORT, UploadTargetType.SHORTS -> {
                    val content = CreatorContentEntity(
                        id = item.id,
                        channelId = item.channelId ?: item.userId,
                        channelName = item.channelName ?: "nexora Channel",
                        channelHandle = "",
                        userId = item.userId,
                        contentType = "SHORT",
                        title = item.title,
                        description = item.description,
                        videoPath = item.fileUri,
                        thumbnailUri = item.thumbnailUri ?: "thumb_0.jpg",
                        visibility = item.visibility,
                        isMadeForKids = item.audience == "MADE_FOR_KIDS",
                        category = item.category,
                        durationSeconds = 15,
                        status = "PUBLISHED",
                        uploadProgress = 100,
                        createdAt = System.currentTimeMillis()
                    )
                    database.creatorContentDao().insertContent(content)
                }
                UploadTargetType.POST_IMAGE -> {
                    val content = CreatorContentEntity(
                        id = item.id,
                        channelId = item.channelId ?: item.userId,
                        channelName = item.channelName ?: "nexora Channel",
                        channelHandle = "",
                        userId = item.userId,
                        contentType = "POST",
                        title = item.title.take(60),
                        postText = item.description.ifBlank { item.title },
                        postImageUri = item.fileUri,
                        visibility = item.visibility,
                        status = "PUBLISHED",
                        uploadProgress = 100,
                        createdAt = System.currentTimeMillis()
                    )
                    database.creatorContentDao().insertContent(content)
                }
                else -> { }
            }
        } catch (_: Exception) { }
    }

    fun retryUpload(id: String) {
        val existing = _uploads.value.find { it.id == id } ?: return
        activeJobs[id]?.cancel()

        val refreshed = existing.copy(
            status = UploadStatus.QUEUED,
            progressPercent = 0,
            errorMessage = null,
            retryCount = existing.retryCount + 1,
            startedAt = System.currentTimeMillis()
        )

        updateItem(id) { refreshed }

        val job = scope.launch {
            processUpload(refreshed)
        }
        activeJobs[id] = job
    }

    fun cancelUpload(id: String) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        updateItem(id) {
            it.copy(status = UploadStatus.CANCELLED, currentSpeed = "Cancelled", uploadSpeed = "Cancelled")
        }
    }

    fun removeUpload(id: String) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        _uploads.update { list -> list.filterNot { it.id == id } }
    }

    fun updateSettings(newSettings: UploadSettings) {
        _settings.value = newSettings
    }

    private fun updateItem(id: String, transform: (UploadTask) -> UploadTask) {
        _uploads.update { list ->
            list.map { item -> if (item.id == id) transform(item) else item }
        }
    }
}
