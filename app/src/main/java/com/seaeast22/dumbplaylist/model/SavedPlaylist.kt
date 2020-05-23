package com.seaeast22.dumbplaylist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savedplaylist")
data class SavedPlaylist(
    @PrimaryKey
    val playlistId: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?) {
    override fun toString() = playlistId
}