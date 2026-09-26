package com.example.upload.model

import java.util.Locale
import java.util.UUID

enum class UploadStatus {
    QUEUED,
    UPLOADING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class UploadTask(
    val id: String = UUID.randomUUID().toString(),
    val targetType: UploadTargetType,
    val title: String,
    val description: String = "",
    val mediaUri: String = "",
    val fileUri: String = mediaUri,
    val fileName: String = "",
    val fileSizeFormatted: String = "",
    val mimeType: String = "",
    val fileSizeBytes: Long = 0L,
    val mediaType: String = if (mimeType.startsWith("video/")) "VIDEO" else "IMAGE",
    val thumbnailUri: String? = null,
    val durationFormatted: String? = null,
    val resolutionFormatted: String? = null,
    val status: UploadStatus = UploadStatus.QUEUED,
    val progressPercent: Int = 0,
    val bytesUploaded: Long = 0L,
    val totalBytes: Long = fileSizeBytes,
    val uploadSpeed: String = "",
    val currentSpeed: String = uploadSpeed,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long = createdAt,
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val retryCount: Int = 0,
    val channelId: String? = null,
    val channelName: String? = null,
    val userId: String = "",
    val category: String = "Gaming",
    val visibility: String = "PUBLIC",
    val audience: String = "NOT_FOR_KIDS",
    val tags: String = "",
    val associatedContentId: String? = null
) {
    val formattedProgress: String
        get() = "$progressPercent%"

    val formattedSize: String
        get() {
            if (fileSizeFormatted.isNotBlank()) return fileSizeFormatted
            if (fileSizeBytes <= 0) return "0 KB"
            val mb = fileSizeBytes / (1024.0 * 1024.0)
            return if (mb < 1.0) {
                "${fileSizeBytes / 1024} KB"
            } else {
                String.format(Locale.US, "%.1f MB", mb)
            }
        }
}

typealias UploadItem = UploadTask
