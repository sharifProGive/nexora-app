package com.example.creator.repository

import android.content.Context
import com.example.creator.model.UploadStatusStep
import com.example.data.database.NexoraDatabase
import com.example.data.model.ChannelEntity
import com.example.data.model.CreatorContentEntity
import com.example.data.model.UserEntity
import com.example.security.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class CreatorRepository(private val database: NexoraDatabase) {

    private val channelDao = database.channelDao()
    private val creatorContentDao = database.creatorContentDao()
    private val userDao = database.userDao()

    suspend fun getOrCreateChannel(user: UserEntity): ChannelEntity = withContext(Dispatchers.IO) {
        val existing = channelDao.getChannelByUserId(user.id)
        if (existing != null) {
            return@withContext existing
        }

        val channelName = user.channelName.orEmpty()
        val channelHandle = user.channelHandle.orEmpty()
        val channelBio = user.channelBio.orEmpty()

        val newChannel = ChannelEntity(
            channelId = UUID.randomUUID().toString(),
            userId = user.id,
            name = channelName,
            handle = channelHandle,
            description = channelBio,
            avatarColorHex = "#06B6D4",
            category = "Gaming",
            language = "English",
            countryRegion = "United States"
        )
        channelDao.insertChannel(newChannel)
        newChannel
    }

    fun observeChannel(userId: String): Flow<ChannelEntity?> {
        return channelDao.observeChannelByUserId(userId)
    }

    suspend fun isHandleAvailable(handle: String, currentChannelId: String): Boolean = withContext(Dispatchers.IO) {
        val clean = SecurityUtils.formatHandle(handle)
        val count = channelDao.countChannelsWithHandle(clean, currentChannelId)
        count == 0
    }

    suspend fun updateChannel(channel: ChannelEntity): Result<ChannelEntity> = withContext(Dispatchers.IO) {
        try {
            val cleanHandle = SecurityUtils.formatHandle(channel.handle, preserveCase = true)
            val updated = channel.copy(
                handle = cleanHandle,
                updatedAt = System.currentTimeMillis()
            )
            channelDao.updateChannel(updated)

            // Keep user entity synchronized
            userDao.updateChannel(
                userId = updated.userId,
                hasChannel = true,
                channelName = updated.name,
                channelHandle = updated.handle,
                channelBio = updated.description,
                channelsCount = 1
            )

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observePublishedContent(channelId: String): Flow<List<CreatorContentEntity>> {
        return creatorContentDao.observePublishedContentForChannel(channelId)
    }

    fun observeDrafts(channelId: String): Flow<List<CreatorContentEntity>> {
        return creatorContentDao.observeDraftsForChannel(channelId)
    }

    suspend fun saveDraft(content: CreatorContentEntity): Result<CreatorContentEntity> = withContext(Dispatchers.IO) {
        try {
            val draft = content.copy(
                status = "DRAFT",
                uploadProgress = 0,
                updatedAt = System.currentTimeMillis()
            )
            creatorContentDao.insertContent(draft)
            Result.success(draft)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            creatorContentDao.deleteContent(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContentVisibility(id: String, visibility: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            creatorContentDao.updateVisibility(id, visibility)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContentDetails(
        id: String,
        title: String,
        description: String,
        hashtags: String,
        visibility: String,
        isMadeForKids: Boolean,
        location: String?,
        allowComments: Boolean,
        category: String,
        playlist: String?,
        isAiGenerated: Boolean
    ): Result<CreatorContentEntity> = withContext(Dispatchers.IO) {
        try {
            val existing = creatorContentDao.getContentById(id)
                ?: return@withContext Result.failure(Exception("Content not found"))
            val updated = existing.copy(
                title = title.trim(),
                description = description.trim(),
                hashtags = hashtags.trim(),
                visibility = visibility,
                isMadeForKids = isMadeForKids,
                location = location?.trim(),
                allowComments = allowComments,
                category = category,
                playlist = playlist,
                isAiGenerated = isAiGenerated,
                updatedAt = System.currentTimeMillis()
            )
            creatorContentDao.updateContent(updated)
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeUploadProcess(
        content: CreatorContentEntity,
        simulateFailure: Boolean = false,
        onProgress: suspend (step: UploadStatusStep, percent: Int, message: String) -> Unit
    ): Result<CreatorContentEntity> = withContext(Dispatchers.IO) {
        try {
            // First save as UPLOADING
            val inFlight = content.copy(
                status = "UPLOADING",
                uploadProgress = 0,
                updatedAt = System.currentTimeMillis()
            )
            creatorContentDao.insertContent(inFlight)

            // Step 1: Preparing
            onProgress(UploadStatusStep.PREPARING, 5, "Preparing upload...")
            delay(400)

            // Step 2: Uploading 25% -> 50% -> 75% -> 100%
            onProgress(UploadStatusStep.UPLOADING, 25, "Uploading 25%")
            creatorContentDao.updateStatus(content.id, "UPLOADING", 25)
            delay(500)

            if (simulateFailure) {
                onProgress(UploadStatusStep.FAILED, 25, "Upload failed.")
                creatorContentDao.updateStatus(content.id, "FAILED", 25)
                return@withContext Result.failure(Exception("Network error: Upload interrupted."))
            }

            onProgress(UploadStatusStep.UPLOADING, 50, "Uploading 50%")
            creatorContentDao.updateStatus(content.id, "UPLOADING", 50)
            delay(500)

            onProgress(UploadStatusStep.UPLOADING, 75, "Uploading 75%")
            creatorContentDao.updateStatus(content.id, "UPLOADING", 75)
            delay(500)

            onProgress(UploadStatusStep.UPLOADING, 100, "Uploading 100%")
            creatorContentDao.updateStatus(content.id, "UPLOADING", 100)
            delay(400)

            // Step 3: Processing
            onProgress(UploadStatusStep.PROCESSING, 100, "Processing video...")
            creatorContentDao.updateStatus(content.id, "PROCESSING", 100)
            delay(600)

            // Step 4: Checking
            onProgress(UploadStatusStep.CHECKING, 100, "Checking content...")
            creatorContentDao.updateStatus(content.id, "CHECKING", 100)
            delay(600)

            // Step 5: Publishing
            onProgress(UploadStatusStep.PUBLISHING, 100, "Publishing...")
            creatorContentDao.updateStatus(content.id, "PUBLISHING", 100)
            delay(500)

            // Completed!
            val published = content.copy(
                status = "PUBLISHED",
                uploadProgress = 100,
                updatedAt = System.currentTimeMillis()
            )
            creatorContentDao.updateContent(published)

            val typeDesc = if (content.contentType == "SHORT") "Short" else if (content.contentType == "VIDEO") "Video" else "Content"
            onProgress(UploadStatusStep.COMPLETED, 100, "Your $typeDesc has been published.")

            Result.success(published)
        } catch (e: Exception) {
            creatorContentDao.updateStatus(content.id, "FAILED", 0)
            onProgress(UploadStatusStep.FAILED, 0, "Upload failed: ${e.message}")
            Result.failure(e)
        }
    }
}
