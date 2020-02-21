package com.example.dumbplaylist.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.databinding.ListItemPlaylistBinding
import com.example.dumbplaylist.model.Playlist

class SavedListAdapter : ListAdapter<Playlist, RecyclerView.ViewHolder>(SavedListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SavedListViewHolder(
            ListItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false),
            parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as SavedListViewHolder).bind(item)
    }

    class SavedListViewHolder(binding: ListItemPlaylistBinding, context: Context)
        : PlaylistViewHolder(binding, context) {

        override fun navigateToVideoList(view: View, playlistId: String) {

        }
    }
}


private class SavedListDiffCallback: DiffUtil.ItemCallback<Playlist>() {
    // 자료구조에 id 가 있으면 id 로 비교 하는 코드로 교환 가능
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.playlistId == newItem.playlistId
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}