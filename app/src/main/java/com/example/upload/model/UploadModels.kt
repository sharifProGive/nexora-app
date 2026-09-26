package com.example.upload.model

import android.net.Uri

data class MediaFileMetadata(
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val formattedSize: String,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
    val formattedDuration: String? = null,
    val resolutionFormatted: String? = null
)

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String, val suggestedAction: String = "") : ValidationResult()
}
