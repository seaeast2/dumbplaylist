package com.example.dumbplaylist.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PlayItemDao {
    @Query("SELECT * FROM playitems")
    fun getPlayItems(): LiveData<List<PlayItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlayItem>)
}