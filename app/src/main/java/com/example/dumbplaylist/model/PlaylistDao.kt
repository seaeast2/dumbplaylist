package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getPlaylists(): LiveData<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Playlist>)

    @Query("DELETE FROM playlists")
    suspend fun deleteAll()
}