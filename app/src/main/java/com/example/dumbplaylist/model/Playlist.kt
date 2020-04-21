package com.example.dumbplaylist.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.bumptech.glide.Glide

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