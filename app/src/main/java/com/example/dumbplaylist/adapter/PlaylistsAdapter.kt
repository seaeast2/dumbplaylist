package com.example.dumbplaylist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.databinding.ListItemPlaylistBinding
import com.example.dumbplaylist.model.Playlist
import com.example.dumbplaylist.ui.SearchedPlaylistsFragmentDirections


class PlaylistAdapter: ListAdapter<Playlist, RecyclerView.ViewHolder>(SearchedListCallback()) {
    // 뷰홀더가 필요할 때마다 호출해서 뷰홀더를 생성한다.
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchListViewHolder(
            ListItemPlaylistBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false),
            viewGroup.context)
    }

    // position 에 해당하는 데이터를 가져다 viewholder 에 들어 있는 item 에 업데이트
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as PlaylistViewHolder).bind(item)
    }

    /*fun updateList(list: List<String>) {
        submitList(list) //
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }*/

    // ViewHolder 가 실제적인 item 껍데기를 가지고 있음.
    // 화면 출력에 필요한 양만큼 생성된다.
    class SearchListViewHolder(binding: ListItemPlaylistBinding, context: Context) 
        : PlaylistViewHolder(binding, context) {
        override fun navigateToVideoList(view: View, playlistId: String) {
            // ID 를 통해 이동
            // SafeArgs 를 통해 이동
            val direction = SearchedPlaylistsFragmentDirections.
                actionSearchedPlaylistsFragmentToPlayingFragment (playlistId)
            view.findNavController().navigate(direction)
        }
    }
}

private class SearchedListCallback : DiffUtil.ItemCallback<Playlist>() {
    // 자료구조에 id 가 있으면 id 로 비교 하는 코드로 교환 가능
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.playlistId == newItem.playlistId
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}