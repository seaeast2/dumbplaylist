package com.example.dumbplaylist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

// Playlist 에 대한 정보
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey @ColumnInfo(name = "id") val playlistId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String) {
    override fun toString() = title
}