package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_sessions")
data class ActiveSessionEntity(
    @PrimaryKey
    val sessionId: String,
    val userId: String,
    val sessionToken: String,
    val authMethod: String, // GOOGLE, EMAIL, PHONE
    val loginTimestamp: Long = System.currentTimeMillis(),
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
