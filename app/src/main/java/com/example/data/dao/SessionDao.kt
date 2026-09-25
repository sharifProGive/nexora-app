package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActiveSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ActiveSessionEntity)

    @Query("SELECT * FROM active_sessions WHERE isActive = 1 ORDER BY loginTimestamp DESC LIMIT 1")
    suspend fun getActiveSession(): ActiveSessionEntity?

    @Query("SELECT * FROM active_sessions WHERE isActive = 1 ORDER BY loginTimestamp DESC LIMIT 1")
    fun observeActiveSession(): Flow<ActiveSessionEntity?>

    @Query("UPDATE active_sessions SET isActive = 0 WHERE isActive = 1")
    suspend fun terminateAllSessions()

    @Update
    suspend fun updateSession(session: ActiveSessionEntity)
}
