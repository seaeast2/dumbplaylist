package com.seaeast22.dumbplaylist.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 *  The Room database for this app
 */

@Database(entities = [Playlist::class, PlaylistItem::class, SavedPlaylist::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // 이거 자동생성되는 함수. 아마도 return Type 으로 타입추론 해서 생성되는 것 같음.
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistItemDao(): PlaylistItemDao
    abstract fun savedPlaylistDao(): SavedPlaylistDao

    companion object {
        // For Singleton instantiation
        @Volatile var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context):AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}


private const val DATABASE_NAME = "myplaylist-db"

