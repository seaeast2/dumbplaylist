package com.example.dumbplaylist.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.databinding.ListItemVideolistBinding
import com.example.dumbplaylist.viewmodel.PlayingViewModel

class VideoListAdapter(private val playSelectedVideo: (videoId:String)->Unit) :
    ListAdapter<PlayingViewModel.VideoItem, RecyclerView.ViewHolder>(PlayItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoListViewHolder(
            ListItemVideolistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false),
            playSelectedVideo)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item : PlayingViewModel.VideoItem? = getItem(position)
        (holder as VideoListViewHolder).bind(item)
    }

    /*fun updateList(list: List<String>) {
        submitList(list) //
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }*/

    class VideoListViewHolder(private val binding: ListItemVideolistBinding,
                              func: (videoId:String)->Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {view ->
                binding.playlistItem?.let {
                    func(it.videoId)
                }
            }
        }

        fun bind(item: PlayingViewModel.VideoItem?) {
            binding.apply {
                playlistItem = item
                executePendingBindings()
            }
        }
    }
}

private class PlayItemDiffCallback : DiffUtil.ItemCallback<PlayingViewModel.VideoItem>() {
    override fun areItemsTheSame(oldItem: PlayingViewModel.VideoItem, newItem: PlayingViewModel.VideoItem): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(oldItem: PlayingViewModel.VideoItem, newItem: PlayingViewModel.VideoItem): Boolean {
        return oldItem == newItem
    }
}