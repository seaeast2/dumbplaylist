package com.example.dumbplaylist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.R
import com.example.dumbplaylist.databinding.ListItemPlaylistBinding
import com.example.dumbplaylist.model.Playlist

class PlaylistAdapter: ListAdapter<Playlist, RecyclerView.ViewHolder>(StringDiffCallback()){
    // 데이터 소스. 외부에서 받아오게 구현 하는게 일반적임

    // position 에 해당하는 데이터를 가져다 viewholder 에 들어 있는 item 에 업데이트
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as PlaylistViewHolder).bind(item)
    }

    // 뷰홀더 생성
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 뷰홀더가 필요할 때마다 이걸 호출해서 뷰홀더를 생성한다.
        return PlaylistViewHolder(ListItemPlaylistBinding.inflate(LayoutInflater.from(viewGroup.context),
            viewGroup, false), viewGroup.context)
    }

    /*fun updateList(list: List<String>) {
        submitList(list) //
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }*/


    // ViewHolder 가 실제적인 item 껍데기를 가지고 있음.
    // 화면 출력에 필요한 양만큼 생성된다.
    class PlaylistViewHolder(private val binding: ListItemPlaylistBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(item : Playlist) {
            binding.apply {
                playlist = item
                executePendingBindings()
            }
        }
    }
}

private class StringDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    // 자료구조에 id 가 있으면 id 로 비교 하는 코드로 교환 가능
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.playlistId == newItem.playlistId
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}