package com.example.dumbplaylist.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.databinding.ListItemVideolistBinding
import com.example.dumbplaylist.model.PlaylistItem

class VideoListAdapter(private val playSelectedVideo: (videoId:String)->Unit) :
    ListAdapter<PlaylistItem, RecyclerView.ViewHolder>(PlayItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoListViewHolder(
            ListItemVideolistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false),
            playSelectedVideo)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item : PlaylistItem? = getItem(position)
        (holder as VideoListViewHolder).bind(item)
    }

    /*fun updateList(list: List<String>) {
        submitList(list) //
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }*/

    class VideoListViewHolder(private val binding: ListItemVideolistBinding, func: (videoId:String)->Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {view ->
                binding.playlistItem?.let {
                    func(it.id)
                }
            }
        }

        fun bind(item: PlaylistItem?) {
            binding.apply {
                playlistItem = item
                executePendingBindings()
            }
        }
    }
}

private class PlayItemDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {
    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem == newItem
    }
}