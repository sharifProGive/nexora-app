package com.example.upload.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.upload.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object MediaFileHelper {

    suspend fun resolveMediaItem(context: Context, uri: Uri): MediaItem = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        var fileName = "media_${System.currentTimeMillis()}"
        var fileSize = 0L

        // 1. Query display name and size from content resolver
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        cursor.getString(nameIndex)?.let { fileName = it }
                    }
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (_: Exception) {}

        // Fallback to Uri path if name unavailable
        if (fileName.isBlank() || fileName.startsWith("media_")) {
            uri.lastPathSegment?.let { segment ->
                val clean = segment.substringAfterLast('/')
                if (clean.isNotBlank()) fileName = clean
            }
        }

        // 2. Resolve Mime Type
        var mimeType = contentResolver.getType(uri) ?: ""
        if (mimeType.isBlank()) {
            val extension = fileName.substringAfterLast('.', "").lowercase()
            mimeType = when (extension) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                "mp4" -> "video/mp4"
                "mov" -> "video/quicktime"
                "webm" -> "video/webm"
                "mkv" -> "video/x-matroska"
                else -> "application/octet-stream"
            }
        }

        // 3. Cache media file into app's persistent uploads cache
        val cacheDir = File(context.cacheDir, "uploads").apply { mkdirs() }
        val ext = if (fileName.contains('.')) ".${fileName.substringAfterLast('.')}" else ""
        val cachedFile = File(cacheDir, "cached_${UUID.randomUUID()}$ext")

        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cachedFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (fileSize <= 0L) {
                fileSize = cachedFile.length()
            }
        } catch (_: Exception) {}

        var width = 0
        var height = 0
        var durationMs = 0L

        val isVideo = mimeType.startsWith("video/")

        // 4. Extract resolution and duration
        if (isVideo && cachedFile.exists() && cachedFile.length() > 0) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(cachedFile.absolutePath)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationMs = durationStr?.toLongOrNull() ?: 0L
                val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                val rotation = rotationStr?.toIntOrNull() ?: 0

                val rawW = widthStr?.toIntOrNull() ?: 0
                val rawH = heightStr?.toIntOrNull() ?: 0
                if (rotation == 90 || rotation == 270) {
                    width = rawH
                    height = rawW
                } else {
                    width = rawW
                    height = rawH
                }
                retriever.release()
            } catch (_: Exception) {}
        } else if (cachedFile.exists() && cachedFile.length() > 0) {
            try {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(cachedFile.absolutePath, options)
                width = options.outWidth
                height = options.outHeight
            } catch (_: Exception) {}
        }

        MediaItem(
            uri = Uri.fromFile(cachedFile),
            fileName = fileName,
            mimeType = mimeType,
            fileSizeBytes = fileSize,
            durationMs = durationMs,
            width = width,
            height = height,
            isVideo = isVideo,
            localCachedPath = cachedFile.absolutePath
        )
    }

    /**
     * Crops and saves a transformed image (scale, translation, rotation) into a persistent file
     */
    suspend fun cropAndSaveImage(
        context: Context,
        sourceUri: Uri,
        rotationDegrees: Float,
        zoomScale: Float,
        panOffsetX: Float,
        panOffsetY: Float,
        aspectRatio: Float = 1.0f // 1.0 for profile square, ~3.0 or 16/9 for banner
    ): MediaItem = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = try {
            context.contentResolver.openInputStream(sourceUri)
        } catch (_: Exception) {
            null
        }

        val originalBitmap = inputStream?.use { BitmapFactory.decodeStream(it) }
            ?: Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)

        val targetWidth = 720
        val targetHeight = (targetWidth / aspectRatio).toInt().coerceAtLeast(200)

        val matrix = Matrix().apply {
            postRotate(rotationDegrees)
            postScale(zoomScale, zoomScale)
            postTranslate(panOffsetX, panOffsetY)
        }

        val resultBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(resultBitmap)

        // Draw centered and transformed
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
        val srcRect = android.graphics.Rect(0, 0, originalBitmap.width, originalBitmap.height)
        val dstRect = android.graphics.RectF(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat())

        // Save original transform
        canvas.save()
        canvas.concat(matrix)
        canvas.drawBitmap(originalBitmap, null, dstRect, paint)
        canvas.restore()

        val cacheDir = File(context.cacheDir, "uploads").apply { mkdirs() }
        val croppedFile = File(cacheDir, "crop_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")

        FileOutputStream(croppedFile).use { out ->
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }

        MediaItem(
            uri = Uri.fromFile(croppedFile),
            fileName = croppedFile.name,
            mimeType = "image/jpeg",
            fileSizeBytes = croppedFile.length(),
            width = targetWidth,
            height = targetHeight,
            isVideo = false,
            localCachedPath = croppedFile.absolutePath
        )
    }
}
