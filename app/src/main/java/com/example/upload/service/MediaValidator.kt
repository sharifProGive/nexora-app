package com.example.upload.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.upload.model.FileValidationResult
import com.example.upload.model.MediaItem
import com.example.upload.model.UploadTargetType

object MediaValidator {

    fun validateMedia(item: MediaItem, targetType: UploadTargetType): FileValidationResult {
        // 1. Integrity check
        if (item.fileSizeBytes <= 0L) {
            return FileValidationResult.Invalid(
                reason = "This file can't be uploaded because it appears corrupted or empty (0 bytes).",
                fileName = item.fileName
            )
        }

        // 2. Supported format check
        val isMimeAllowed = (targetType.allowedMimeTypes.isEmpty() && targetType.allowedMimePrefixes.isEmpty()) ||
                targetType.allowedMimeTypes.any { allowed ->
                    allowed == "*/*" ||
                    item.mimeType.equals(allowed, ignoreCase = true) ||
                    (allowed.endsWith("/*") && item.mimeType.startsWith(allowed.removeSuffix("/*"), ignoreCase = true))
                } || targetType.allowedMimePrefixes.any { prefix ->
                    item.mimeType.startsWith(prefix, ignoreCase = true)
                }

        if (!isMimeAllowed) {
            val supportedFormatsText = if (targetType.allowedExtensions.isNotEmpty()) {
                targetType.allowedExtensions.joinToString(", ") { it.uppercase() }
            } else {
                targetType.allowedMimeTypes.joinToString(", ") { it.substringAfter('/') }.uppercase()
            }
            return FileValidationResult.Invalid(
                reason = "This file can't be uploaded because '${item.mimeType}' is not supported for ${targetType.title}. Supported formats: $supportedFormatsText.",
                fileName = item.fileName
            )
        }

        // 3. File size check
        if (item.fileSizeBytes > targetType.maxSizeBytes) {
            val maxMb = targetType.maxSizeBytes / (1024 * 1024)
            return FileValidationResult.Invalid(
                reason = "This file can't be uploaded because its file size (${item.formattedSize}) exceeds the limit of $maxMb MB for ${targetType.title}.",
                fileName = item.fileName
            )
        }

        // 4. Video duration checks
        if (targetType == UploadTargetType.SHORTS || targetType == UploadTargetType.SHORT) {
            if (item.durationMs > 60_500L) {
                return FileValidationResult.Invalid(
                    reason = "This file can't be uploaded because Shorts must be 60 seconds or less (selected video is ${item.formattedDuration}).",
                    fileName = item.fileName
                )
            }
        }

        if (targetType == UploadTargetType.LONG_VIDEO) {
            if (item.durationMs in 1..999L) {
                return FileValidationResult.Invalid(
                    reason = "This file can't be uploaded because the video duration is too short for a video upload.",
                    fileName = item.fileName
                )
            }
        }

        return FileValidationResult.Valid(item)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return true
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
