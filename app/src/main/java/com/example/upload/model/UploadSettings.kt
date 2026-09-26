package com.example.upload.model

data class UploadSettings(
    val defaultVisibility: String = "PUBLIC", // PUBLIC, UNLISTED, PRIVATE
    val defaultAudience: String = "NOT_FOR_KIDS", // NOT_FOR_KIDS, MADE_FOR_KIDS
    val defaultCategory: String = "Gaming",
    val defaultUploadQuality: String = "ORIGINAL_1080P", // ORIGINAL_1080P, HIGH_720P, DATA_SAVER_480P
    val defaultQuality: String = defaultUploadQuality,
    val wifiOnly: Boolean = false,
    val networkPreference: String = if (wifiOnly) "Wi-Fi Only" else "Any Network",
    val uploadNotifications: Boolean = true,
    val thumbnailDefaults: String = "AUTO_GENERATE",
    val autoGenerateThumbnails: Boolean = true,
    val processingPreference: String = "STANDARD",
    val autoRetryOnReconnect: Boolean = true,
    val concurrentUploadsLimit: Int = 2,
    val autoCleanupCachedMedia: Boolean = true,
    val hardwareAcceleration: Boolean = true,
    val backgroundChunking: Boolean = true,
    val advancedEncodingPreset: String = "H.264 / AAC High Profile"
)
