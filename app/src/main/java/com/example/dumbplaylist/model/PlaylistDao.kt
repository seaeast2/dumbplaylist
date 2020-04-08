package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY playlists.idx ASC")
    fun getAll(): LiveData<List<Playlist>>

//    @Query("SELECT * FROM playlists WHERE playlists.searchQuery = :searchQuery")
//    fun hasSameSearchQuery(searchQuery: String): List<Playlist>
//
//    @Query("SELECT * FROM playlists WHERE playlists.pageToken = :pageToken")
//    fun hasPageToken(pageToken: String): List<Playlist>

    @Query("SELECT COUNT(playlists.idx) FROM playlists")
    suspend fun getItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Playlist>)

    @Query("DELETE FROM playlists")
    suspend fun deleteAll()
}