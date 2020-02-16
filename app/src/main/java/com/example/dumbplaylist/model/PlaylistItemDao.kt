package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PlaylistItemDao {
    @Query("SELECT * FROM playlistItems ORDER BY playlistItems.idx ASC")
    fun getPlaylistItems(): LiveData<List<PlaylistItem>>

//    @Query("SELECT * FROM playlistItems WHERE playlistId = :playlistId")
//    fun hasSamePlaylistId(playlistId: String): List<PlaylistItem>
//
//    @Query("SELECT * FROM playlistItems WHERE pageToken = :pageToken")
//    fun hasPageToken(pageToken: String): List<PlaylistItem>

    @Query("SELECT COUNT(playlistItems.idx) FROM playlistItems")
    suspend fun getItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlaylistItem>)

    @Query("DELETE FROM playlistItems")
    suspend fun deleteAll()
}