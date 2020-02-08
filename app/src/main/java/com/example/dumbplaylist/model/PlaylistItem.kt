package com.example.dumbplaylist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 개별 play item 정보
@Entity(tableName = "playlistItems")
data class PlaylistItem (
    @PrimaryKey
    val id: String,
    val index: Int,
    val playlistId: String,
    val pageToken: String?,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?) {
        override fun toString() = id
    }