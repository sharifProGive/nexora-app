package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["handle"], unique = true),
        Index(value = ["email"], unique = false),
        Index(value = ["phone"], unique = false)
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val authProvider: String, // GOOGLE, EMAIL, PHONE
    val identifier: String, // email or phone number
    val email: String? = null,
    val phone: String? = null,
    val googleId: String? = null,
    val name: String, // Main Profile Name
    val handle: String, // Unique @handle
    val passwordHash: String,
    val passwordSalt: String,
    val bio: String = "",
    val avatarIndex: Int = 0,
    val avatarColorHex: String = "#6366F1",
    val profilePictureUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = true,
    // Profile Privacy (Step 1A)
    val isPrivateProfile: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    // Video Channel (Step 1A - Separate entity from Personal Profile)
    val hasChannel: Boolean = false,
    val channelName: String? = null,
    val channelHandle: String? = null,
    val channelBio: String? = null,
    // Future-ready fields for NEXORA Video, Chat, Social, Creator Studio
    val videoChannelsCount: Int = 0,
    val isCreator: Boolean = false,
    val accountTier: String = "STANDARD", // STANDARD, CREATOR, PRO
    val status: String = "ACTIVE"
)
