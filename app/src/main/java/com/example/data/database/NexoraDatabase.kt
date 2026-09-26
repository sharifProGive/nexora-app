package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ChannelDao
import com.example.data.dao.CreatorContentDao
import com.example.data.dao.SessionDao
import com.example.data.dao.UserDao
import com.example.data.dao.VerificationDao
import com.example.data.model.ActiveSessionEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.CreatorContentEntity
import com.example.data.model.UserEntity
import com.example.data.model.VerificationCodeEntity

@Database(
    entities = [
        UserEntity::class,
        VerificationCodeEntity::class,
        ActiveSessionEntity::class,
        ChannelEntity::class,
        CreatorContentEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class NexoraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun verificationDao(): VerificationDao
    abstract fun sessionDao(): SessionDao
    abstract fun channelDao(): ChannelDao
    abstract fun creatorContentDao(): CreatorContentDao

    companion object {
        @Volatile
        private var INSTANCE: NexoraDatabase? = null

        fun getInstance(context: Context): NexoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexoraDatabase::class.java,
                    "nexora_platform.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
