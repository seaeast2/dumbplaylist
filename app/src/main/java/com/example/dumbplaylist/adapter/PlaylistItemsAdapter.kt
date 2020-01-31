package com.example.dumbplaylist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.databinding.ListItemVideolistBinding
import com.example.dumbplaylist.model.PlaylistItem

// PagedList 적용된 RecyclerView Adapter
class PlaylistItemsAdapter :
    PagedListAdapter<PlaylistItem, RecyclerView.ViewHolder>(PlaylistItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlaylistItemsViewHolder(ListItemVideolistBinding.inflate(LayoutInflater.from(parent.context), parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val playlistItem: PlaylistItem? = getItem(position)
        (holder as PlaylistItemsViewHolder).bind(playlistItem)
    }

    class PlaylistItemsViewHolder(private val binding: ListItemVideolistBinding,
                                  private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaylistItem?) {
            binding.apply {
                videolist = item
                binding.executePendingBindings()
            }
        }
    }
}


private class PlaylistItemDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {
    // 자료구조에 id 가 있으면 id 로 비교 하는 코드로 교환 가능
    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem == newItem
    }
}



