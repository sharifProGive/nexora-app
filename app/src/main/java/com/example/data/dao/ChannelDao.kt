package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels WHERE userId = :userId LIMIT 1")
    suspend fun getChannelByUserId(userId: String): ChannelEntity?

    @Query("SELECT * FROM channels WHERE userId = :userId LIMIT 1")
    fun observeChannelByUserId(userId: String): Flow<ChannelEntity?>

    @Query("SELECT * FROM channels WHERE channelId = :channelId LIMIT 1")
    suspend fun getChannelById(channelId: String): ChannelEntity?

    @Query("SELECT * FROM channels WHERE LOWER(handle) = LOWER(:handle) LIMIT 1")
    suspend fun getChannelByHandle(handle: String): ChannelEntity?

    @Query("SELECT COUNT(*) FROM channels WHERE LOWER(handle) = LOWER(:handle) AND channelId != :excludeChannelId")
    suspend fun countChannelsWithHandle(handle: String, excludeChannelId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    @Query("DELETE FROM channels WHERE channelId = :channelId")
    suspend fun deleteChannel(channelId: String)
}
