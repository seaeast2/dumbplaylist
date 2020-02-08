package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY playlists.`index` ASC")
    fun getPlaylists(): LiveData<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE playlists.searchQuery = :searchQuery")
    suspend fun hasSameSearchQuery(searchQuery: String): List<Playlist>

    @Query("SELECT * FROM playlists WHERE playlists.pageToken = :pageToken")
    suspend fun hasPageToken(pageToken: String): List<Playlist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Playlist>)

    @Query("DELETE FROM playlists")
    suspend fun deleteAll()
}