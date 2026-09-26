package com.example.upload.model

import android.net.Uri
import java.util.Locale

data class MediaItem(
    val uri: Uri,
    val fileName: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val durationMs: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val isVideo: Boolean = mimeType.startsWith("video/"),
    val localCachedPath: String? = null
) {
    val formattedSize: String
        get() {
            if (fileSizeBytes <= 0) return "0 KB"
            val kb = fileSizeBytes / 1024.0
            if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
            val mb = kb / 1024.0
            if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
            val gb = mb / 1024.0
            return String.format(Locale.US, "%.2f GB", gb)
        }

    val formattedDuration: String
        get() {
            if (durationMs <= 0) return ""
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            return if (hours > 0) {
                String.format(Locale.US, "%d:%02d:%02d", hours, minutes % 60, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
        }

    val formattedResolution: String
        get() = if (width > 0 && height > 0) "${width}x${height}" else ""
}

sealed class FileValidationResult {
    data class Valid(val item: MediaItem) : FileValidationResult()
    data class Invalid(
        val reason: String,
        val fileName: String = "",
        val canRetry: Boolean = true
    ) : FileValidationResult()
}
