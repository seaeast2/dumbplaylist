package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query

@Dao
interface SavedPlaylistDao {
    @Query("SELECT * FROM savedplaylist ORDER BY savedplaylist.idx ASC")
    fun getSavedPlaylist(): LiveData<List<SavedPlaylist>>

    @Query("SELECT COUNT(savedplaylist.idx) FROM savedplaylist")
    suspend fun getItemCount(): Int

    @Delete(entity = SavedPlaylist::class)
    suspend fun deleteSavedPlaylist(vararg savedPlaylists: IndexAndPlaylistId)
}

data class IndexAndPlaylistId(val idx: Int, val playlistId: String)