package com.example.dumbplaylist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savedplaylist")
data class SavedPlaylist(
    @PrimaryKey
    val idx: Int,
    val playlistId: String,
    val title: String?,
    val thumbnailUrl: String?) {
    override fun toString() = playlistId
}