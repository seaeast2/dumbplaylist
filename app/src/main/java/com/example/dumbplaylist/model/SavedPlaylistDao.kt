package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SavedPlaylistDao {
    @Query("SELECT * FROM savedplaylist")
    fun getAll(): LiveData<List<SavedPlaylist>>

    @Query("SELECT COUNT(savedplaylist.playlistId) FROM savedplaylist WHERE savedplaylist.playlistId = :playlistId")
    fun findByPlaylistId(playlistId: String): Int

    @Query("SELECT COUNT(savedplaylist.playlistId) FROM savedplaylist")
    suspend fun getItemCount(): Int

    @Delete
    suspend fun deleteSavedPlaylist(savedPlaylist: SavedPlaylist)

    @Insert
    suspend fun insertSavedPlaylist(savedPlaylist: SavedPlaylist)
}