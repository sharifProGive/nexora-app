package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CreatorContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreatorContentDao {

    @Query("SELECT * FROM creator_content WHERE channelId = :channelId AND status = 'PUBLISHED' ORDER BY createdAt DESC")
    fun observePublishedContentForChannel(channelId: String): Flow<List<CreatorContentEntity>>

    @Query("SELECT * FROM creator_content WHERE channelId = :channelId AND contentType = :type AND status = 'PUBLISHED' ORDER BY createdAt DESC")
    fun observePublishedByType(channelId: String, type: String): Flow<List<CreatorContentEntity>>

    @Query("SELECT * FROM creator_content WHERE channelId = :channelId AND status = 'DRAFT' ORDER BY updatedAt DESC")
    fun observeDraftsForChannel(channelId: String): Flow<List<CreatorContentEntity>>

    @Query("SELECT * FROM creator_content WHERE channelId = :channelId ORDER BY createdAt DESC")
    fun observeAllContentForChannel(channelId: String): Flow<List<CreatorContentEntity>>

    @Query("SELECT * FROM creator_content WHERE id = :id LIMIT 1")
    suspend fun getContentById(id: String): CreatorContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: CreatorContentEntity)

    @Update
    suspend fun updateContent(content: CreatorContentEntity)

    @Query("UPDATE creator_content SET status = :status, uploadProgress = :progress, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, progress: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE creator_content SET visibility = :visibility, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateVisibility(id: String, visibility: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM creator_content WHERE id = :id")
    suspend fun deleteContent(id: String)
}
