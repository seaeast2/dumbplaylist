package com.seaeast22.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PlaylistItemDao {
    @Query("SELECT * FROM playlistItems ORDER BY playlistItems.idx ASC")
    fun getAll(): LiveData<List<PlaylistItem>>

//    @Query("SELECT * FROM playlistItems ORDER BY playlistItems.idx DESC LIMIT 1")
//    fun getLastItemNoneSuspend(): PlaylistItem?

    @Query("SELECT * FROM playlistItems ORDER BY playlistItems.idx DESC LIMIT 1")
    suspend fun getLastItem(): PlaylistItem?

    @Query("SELECT COUNT(playlistItems.idx) FROM playlistItems")
    fun getItemCountNoneSuspend(): Int

    @Query("SELECT COUNT(playlistItems.idx) FROM playlistItems")
    suspend fun getItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlaylistItem>)

    @Query("DELETE FROM playlistItems")
    suspend fun deleteAll()
}