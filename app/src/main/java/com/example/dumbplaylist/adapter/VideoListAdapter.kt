package com.example.dumbplaylist.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.R
import com.example.dumbplaylist.databinding.ListItemVideolistBinding
import com.example.dumbplaylist.model.PlayItem

class VideoListAdapter: ListAdapter<PlayItem, RecyclerView.ViewHolder>(PlayItemDiffCallback()) {

    // ViewHolder 생성
    // ViewHolder 가 필요할 때마다 하나씩 생성한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoListViewHolder(ListItemVideolistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false), parent.context)
    }

    // ViewHolder 재활용
    // 생성된 ViewHolder 에 데이터를 연결 시켜준다.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // submitList 를 통해 내부적으로 list 를 들고 있음.
        // 필요한 position 을 자동 계산하여 여기서 callback 으로 불러 주는것임.
        val item = getItem(position)
        (holder as VideoListViewHolder).bind(item)
    }

    /*fun updateList(list: List<String>) {
        submitList(list) //
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }*/

    class VideoListViewHolder(private val binding: ListItemVideolistBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                navigateToPlayerView(it)
            }
        }

        // 화면 이동
        private fun navigateToPlayerView(view: View) {
            view.findNavController().navigate(R.id.action_videoListFragment_to_youtubePlayerFragment)
        }

        fun bind(item: PlayItem) {
            binding.apply {
                videolist = item
                executePendingBindings()
            }
        }
    }
}




private class PlayItemDiffCallback : DiffUtil.ItemCallback<PlayItem>() {
    // 자료구조에 id 가 있으면 id 로 비교 하는 코드로 교환 가능
    override fun areItemsTheSame(oldItem: PlayItem, newItem: PlayItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlayItem, newItem: PlayItem): Boolean {
        return oldItem == newItem
    }
}