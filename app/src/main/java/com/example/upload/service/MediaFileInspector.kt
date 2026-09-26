package com.example.upload.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.upload.model.MediaFileMetadata
import com.example.upload.model.UploadTargetType
import com.example.upload.model.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale
import java.util.UUID

object MediaFileInspector {

    suspend fun inspectUri(context: Context, uri: Uri): MediaFileMetadata = withContext(Dispatchers.IO) {
        var name = "selected_file"
        var sizeBytes = 0L

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex) ?: name
                    }
                    if (sizeIndex != -1) {
                        sizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (_: Exception) { }

        // If size not resolved from query, try opening stream
        if (sizeBytes <= 0) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    sizeBytes = stream.available().toLong()
                }
            } catch (_: Exception) { }
        }

        val rawMimeType = context.contentResolver.getType(uri)
            ?: guessMimeTypeFromName(name)

        var width: Int? = null
        var height: Int? = null
        var durationMs: Long? = null

        // If Image: resolve resolution
        if (rawMimeType.startsWith("image/")) {
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(input, null, options)
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        width = options.outWidth
                        height = options.outHeight
                    }
                }
            } catch (_: Exception) { }
        }

        // If Video: resolve duration and resolution
        if (rawMimeType.startsWith("video/")) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationMs = durationStr?.toLongOrNull()

                val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)

                val rawW = widthStr?.toIntOrNull()
                val rawH = heightStr?.toIntOrNull()
                val rotation = rotationStr?.toIntOrNull() ?: 0

                if (rawW != null && rawH != null) {
                    if (rotation == 90 || rotation == 270) {
                        width = rawH
                        height = rawW
                    } else {
                        width = rawW
                        height = rawH
                    }
                }
            } catch (_: Exception) {
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) { }
            }
        }

        val formattedSize = formatFileSize(sizeBytes)
        val formattedDuration = durationMs?.let { formatDuration(it) }
        val resolutionFormatted = if (width != null && height != null) "${width}x${height}" else null

        MediaFileMetadata(
            uri = uri,
            name = name,
            mimeType = rawMimeType,
            sizeBytes = sizeBytes,
            formattedSize = formattedSize,
            width = width,
            height = height,
            durationMs = durationMs,
            formattedDuration = formattedDuration,
            resolutionFormatted = resolutionFormatted
        )
    }

    fun validateFile(metadata: MediaFileMetadata, target: UploadTargetType): ValidationResult {
        // 1. File Type / MIME prefix check
        val isMimeAllowed = target.allowedMimePrefixes.any { prefix ->
            metadata.mimeType.startsWith(prefix, ignoreCase = true)
        }
        val fileExtension = metadata.name.substringAfterLast(".", "").lowercase()
        val isExtAllowed = target.allowedExtensions.contains(fileExtension)

        if (!isMimeAllowed && !isExtAllowed) {
            return ValidationResult.Invalid(
                reason = "This file can't be uploaded because '${metadata.mimeType}' is not supported for ${target.title}. Allowed formats: ${target.allowedExtensions.joinToString(", ")}.",
                suggestedAction = "Choose another file"
            )
        }

        // 2. File size limit
        if (metadata.sizeBytes > target.maxSizeBytes) {
            val maxFormatted = formatFileSize(target.maxSizeBytes)
            return ValidationResult.Invalid(
                reason = "This file can't be uploaded because it exceeds the maximum size limit of $maxFormatted (selected file is ${metadata.formattedSize}).",
                suggestedAction = "Select a smaller or compressed file"
            )
        }

        // 3. Minimum size check (corrupted / 0 bytes file)
        if (metadata.sizeBytes <= 0) {
            return ValidationResult.Invalid(
                reason = "This file can't be uploaded because it is empty or corrupted (0 bytes).",
                suggestedAction = "Choose another file"
            )
        }

        // 4. Video duration limits (for Shorts: max 60s, for Long Video: min 3s)
        if (target == UploadTargetType.SHORT && metadata.durationMs != null) {
            val maxDuration = target.maxDurationMs ?: 60_000L
            if (metadata.durationMs > maxDuration) {
                return ValidationResult.Invalid(
                    reason = "This file can't be uploaded because Shorts must be 60 seconds or less (selected video is ${metadata.formattedDuration}).",
                    suggestedAction = "Trim video or upload as Long Video"
                )
            }
        }

        if (target == UploadTargetType.LONG_VIDEO && metadata.durationMs != null) {
            val minDuration = target.minDurationMs ?: 3_000L
            if (metadata.durationMs < minDuration) {
                return ValidationResult.Invalid(
                    reason = "This file can't be uploaded because videos must be at least 3 seconds long.",
                    suggestedAction = "Select a longer video"
                )
            }
        }

        return ValidationResult.Valid
    }

    suspend fun stageFileToInternalCache(
        context: Context,
        sourceUri: Uri,
        fileName: String
    ): File = withContext(Dispatchers.IO) {
        val uploadsDir = File(context.cacheDir, "uploads").apply { mkdirs() }
        val extension = fileName.substringAfterLast(".", "tmp")
        val uniqueName = "upload_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.$extension"
        val destinationFile = File(uploadsDir, uniqueName)

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Could not read media file stream")

        destinationFile
    }

    suspend fun saveBitmapToInternalCache(
        context: Context,
        bitmap: Bitmap,
        prefix: String = "crop"
    ): File = withContext(Dispatchers.IO) {
        val uploadsDir = File(context.cacheDir, "uploads").apply { mkdirs() }
        val file = File(uploadsDir, "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.0f KB", kb)
            else -> "$bytes B"
        }
    }

    fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val hours = minutes / 60
        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes % 60, seconds)
        } else {
            String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    }

    private fun guessMimeTypeFromName(name: String): String {
        val ext = name.substringAfterLast(".", "").lowercase()
        return when (ext) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "mp4" -> "video/mp4"
            "mov" -> "video/quicktime"
            "mkv" -> "video/x-matroska"
            "webm" -> "video/webm"
            "3gp" -> "video/3gpp"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }
    }
}
