package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PlaylistItemDao {
    @Query("SELECT * FROM playlistItems")
    fun getPlayItems(): LiveData<List<PlaylistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlaylistItem>)

    @Query("DELETE FROM playlistItems")
    suspend fun deleteAll()
}