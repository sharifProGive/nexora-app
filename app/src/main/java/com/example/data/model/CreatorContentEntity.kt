package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "creator_content",
    indices = [
        Index(value = ["channelId"]),
        Index(value = ["userId"]),
        Index(value = ["contentType"]),
        Index(value = ["status"])
    ]
)
data class CreatorContentEntity(
    @PrimaryKey
    val id: String,
    val channelId: String,
    val channelName: String,
    val channelHandle: String,
    val userId: String,
    val contentType: String, // "SHORT", "VIDEO", "POST", "POLL"
    val title: String,
    val description: String = "",
    val hashtags: String = "",
    val videoPath: String? = null,
    val thumbnailUri: String? = null,
    val visibility: String = "PUBLIC", // "PUBLIC", "UNLISTED", "PRIVATE"
    val isMadeForKids: Boolean = false,
    val location: String? = null,
    val allowComments: Boolean = true,
    val isAiGenerated: Boolean = false,
    val status: String = "PUBLISHED", // "DRAFT", "UPLOADING", "UPLOAD_PAUSED", "PROCESSING", "CHECKING", "PUBLISHED", "FAILED"
    val uploadProgress: Int = 100,
    val durationSeconds: Int = 30,

    // Video editor adjustments
    val filterApplied: String = "Normal",
    val speed: Float = 1.0f,
    val audioTrack: String? = null,
    val textOverlay: String? = null,
    val sticker: String? = null,
    val isMuted: Boolean = false,
    val volume: Float = 1.0f,
    val captionsEnabled: Boolean = false,
    val rotationDegrees: Int = 0,
    val trimStartPercent: Float = 0f,
    val trimEndPercent: Float = 1f,

    // Long Video / Advanced Metadata
    val language: String = "English",
    val category: String = "Gaming",
    val playlist: String? = null,
    val scheduledPublishTime: Long? = null,
    val subtitlesInfo: String? = null,
    val chaptersInfo: String? = null,

    // Community Post & Poll details
    val postText: String = "",
    val postImageUri: String? = null,
    val pollQuestion: String? = null,
    val pollOptionsJson: String? = null,
    val pollVotesJson: String? = null,
    val pollDuration: String = "3 days",

    // Metrics
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
