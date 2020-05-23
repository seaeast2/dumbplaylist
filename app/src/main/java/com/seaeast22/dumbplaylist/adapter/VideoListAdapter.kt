package com.seaeast22.dumbplaylist.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seaeast22.dumbplaylist.databinding.ListItemVideolistBinding
import com.seaeast22.dumbplaylist.viewmodel.PlayingViewModel

class VideoListAdapter(private val playSelectedVideo: (videoId:String) -> Unit) :
    ListAdapter<PlayingViewModel.VideoItem, RecyclerView.ViewHolder>(PlayItemDiffCallback()) {
    private var selectedPos : Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoListViewHolder(
            ListItemVideolistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item : PlayingViewModel.VideoItem? = getItem(position)
        val myholder = (holder as VideoListViewHolder)
        myholder.bind(item)

        myholder.binding.setClickListener { _ ->
            myholder.binding.playlistItem?.let {
                playSelectedVideo(it.videoId)
            }
            selectedPos = position
            notifyDataSetChanged()
        }

        // setting up selected state change
        if (selectedPos == position) {
            holder.binding.videoListItem.setBackgroundColor(Color.argb(255, 204, 229, 255))
        }
        else {
            holder.binding.videoListItem.setBackgroundColor(Color.WHITE)
        }
    }

    fun setSelectedPos(position: Int) {
        selectedPos = position
        notifyDataSetChanged()
    }

    /*fun updateList(list: List<String>) {
        submitList(list) //
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }*/

    class VideoListViewHolder(val binding: ListItemVideolistBinding) : RecyclerView.ViewHolder(binding.root) {
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