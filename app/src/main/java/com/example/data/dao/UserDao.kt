package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE LOWER(handle) = LOWER(:handle) LIMIT 1")
    suspend fun getUserByHandle(handle: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(identifier) = LOWER(:identifier) LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE googleId = :googleId LIMIT 1")
    suspend fun getUserByGoogleId(googleId: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE LOWER(handle) = LOWER(:handle)")
    suspend fun countUsersWithHandle(handle: String): Int

    @Query("SELECT COUNT(*) FROM users WHERE LOWER(name) = LOWER(:name)")
    suspend fun countUsersWithName(name: String): Int

    @Query("UPDATE users SET isPrivateProfile = :isPrivate WHERE id = :userId")
    suspend fun updatePrivacy(userId: String, isPrivate: Boolean)

    @Query("UPDATE users SET hasChannel = :hasChannel, channelName = :channelName, channelHandle = :channelHandle, channelBio = :channelBio, videoChannelsCount = :channelsCount WHERE id = :userId")
    suspend fun updateChannel(userId: String, hasChannel: Boolean, channelName: String?, channelHandle: String?, channelBio: String?, channelsCount: Int = 1)

    @Query("UPDATE users SET name = :name, bio = :bio, avatarIndex = :avatarIndex WHERE id = :userId")
    suspend fun updateProfile(userId: String, name: String, bio: String, avatarIndex: Int)

    @Query("UPDATE users SET profilePictureUri = :uri WHERE id = :userId")
    suspend fun updateProfilePicture(userId: String, uri: String?)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
