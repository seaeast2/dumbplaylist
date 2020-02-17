package com.example.dumbplaylist.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.databinding.ListItemPlaylistBinding
import com.example.dumbplaylist.model.Playlist


abstract class PlaylistViewHolder(private val binding: ListItemPlaylistBinding,
                                  private val context: Context)
    : RecyclerView.ViewHolder(binding.root) {

    init {
        // Click 이벤트는 이곳에서 구현
        binding.setClickListener {view ->
            binding.playlist?.let {playlist ->
                navigateToVideoList(view, playlist.playlistId)
            }
        }
    }

    abstract fun navigateToVideoList(view: View, playlistId: String)

    // 데이터를 ViewHolder 에 채워 넣음.
    fun bind(item : Playlist) {
        binding.apply {
            playlist = item
            executePendingBindings()
        }
    }
}