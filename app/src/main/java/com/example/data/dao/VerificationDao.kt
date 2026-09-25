package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VerificationCodeEntity

@Dao
interface VerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCode(code: VerificationCodeEntity): Long

    @Query("SELECT * FROM verification_codes WHERE LOWER(targetIdentifier) = LOWER(:target) AND isUsed = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestActiveCode(target: String): VerificationCodeEntity?

    @Update
    suspend fun updateCode(code: VerificationCodeEntity)

    @Query("UPDATE verification_codes SET isUsed = 1 WHERE LOWER(targetIdentifier) = LOWER(:target)")
    suspend fun markAllUsedForTarget(target: String)
}
