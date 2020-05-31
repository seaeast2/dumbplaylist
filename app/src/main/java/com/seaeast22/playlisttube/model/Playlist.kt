package com.seaeast22.playlisttube.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Playlist 에 대한 정보
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey
    val idx : Int,
    val playlistId: String,
    val searchQuery: String,
    val pageToken: String?,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?) {
        override fun toString() = playlistId
    }