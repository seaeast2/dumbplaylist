package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY playlists.idx ASC")
    fun getAll(): LiveData<List<Playlist>>

//    @Query("SELECT * FROM playlists ORDER BY playlists.idx DESC LIMIT 1")
//    fun getLastItemNoneSuspend(): Playlist?

    @Query("SELECT * FROM playlists ORDER BY playlists.idx DESC LIMIT 1")
    suspend fun getLastItem(): Playlist?

    @Query("SELECT COUNT(playlists.idx) FROM playlists")
    fun getItemCountNoneSuspend(): Int

    @Query("SELECT COUNT(playlists.idx) FROM playlists")
    suspend fun getItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Playlist>)

    @Query("DELETE FROM playlists")
    suspend fun deleteAll()
}