package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
    indices = [
        Index(value = ["userId"], unique = true),
        Index(value = ["handle"], unique = true)
    ]
)
data class ChannelEntity(
    @PrimaryKey
    val channelId: String,
    val userId: String,
    val name: String,
    val handle: String,
    val description: String = "",
    val profilePictureUri: String? = null,
    val bannerUri: String? = null,
    val avatarColorHex: String = "#06B6D4",
    val bannerGradientIndex: Int = 0,
    val category: String = "Gaming",
    val language: String = "English",
    val countryRegion: String = "United States",
    val contactEmail: String = "",
    val showSubscriberCount: Boolean = true,
    val allowComments: Boolean = true,
    val allowSharing: Boolean = true,
    val showInSearch: Boolean = true,
    val allowRecommendations: Boolean = true,
    val defaultVisibility: String = "PUBLIC", // PUBLIC, UNLISTED, PRIVATE
    val defaultAudience: String = "NOT_FOR_KIDS", // MADE_FOR_KIDS, NOT_FOR_KIDS
    val defaultCategory: String = "Gaming",
    val defaultLanguage: String = "English",
    val defaultComments: String = "ALLOW", // ALLOW, DISABLE
    val socialLinksJson: String = "[]",
    val subscriberCount: Int = 0,
    val videoCount: Int = 0,
    val shortsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
