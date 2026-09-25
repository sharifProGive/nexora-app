package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_codes")
data class VerificationCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetIdentifier: String, // email or phone
    val codeHash: String,
    val plainCodePreview: String, // Visible for testing/simulator notifications
    val expiresAt: Long,
    val attemptsCount: Int = 0,
    val maxAttempts: Int = 5,
    val isUsed: Boolean = false,
    val type: String, // "REGISTRATION" or "LOGIN_2FA"
    val createdAt: Long = System.currentTimeMillis()
)
