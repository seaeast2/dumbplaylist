package com.example.dumbplaylist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 개별 play item 정보
@Entity(tableName = "playlistItems")
data class PlaylistItem (
    @PrimaryKey
    val idx: Int,
    val id: String, // video item id
    val playlistId: String, // parent playlist id
    val pageToken: String?,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?) {
        override fun toString() = id
    }