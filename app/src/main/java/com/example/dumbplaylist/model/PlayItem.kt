package com.example.dumbplaylist.model

import androidx.room.Entity
import androidx.room.PrimaryKey


// 개별 play item 정보
@Entity(tableName = "playitems")
data class PlayItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnailUrl: String) {
    override fun toString() = title
}