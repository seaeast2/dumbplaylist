package com.seaeast22.dumbplaylist.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seaeast22.dumbplaylist.databinding.ListItemPlaylistBinding
import com.seaeast22.dumbplaylist.model.Playlist
import com.seaeast22.dumbplaylist.model.SavedPlaylist
import com.seaeast22.dumbplaylist.ui.HomeViewPager2FragmentDirections
import kotlinx.android.parcel.Parcelize


class PlaylistAdapter:
    ListAdapter<Playlist, RecyclerView.ViewHolder>(SearchedListCallback()) {

    // 뷰홀더가 필요할 때마다 호출해서 뷰홀더를 생성한다.
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlaylistViewHolder( ListItemPlaylistBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup, false))
    }

    // position 에 해당하는 데이터를 가져다 viewholder 에 들어 있는 item 에 업데이트
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as PlaylistViewHolder).bind(item)
    }

    fun submitSavedList(list: List<SavedPlaylist>) {
        //submitList(list) //
        val savedPlaylist: List<Playlist> = list.map {
            Playlist(0, it.playlistId, "", null, it.title, it.description, it.thumbnailUrl)
        }
        submitList(savedPlaylist)
        notifyDataSetChanged() // 데이터가 갱신될 때마다 리스트 전체를 갱신해 주어야 함.
    }

    // ViewHolder 가 실제적인 item 껍데기를 가지고 있음.
    // 화면 출력에 필요한 양만큼 생성된다.
    class PlaylistViewHolder(private val binding: ListItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Click 이벤트는 이곳에서 구현
            binding.setClickListener {view ->
                binding.playlist?.let {playlist ->
                    navigateToPlaying(view, playlist)
                }
            }
        }

        fun navigateToPlaying(view: View, playlist: Playlist) {
            // SafeArgs 를 통해 이동
            val selectedPlaylist = SelectedPlaylist(playlist.playlistId, playlist.title,
                    playlist.description, playlist.thumbnailUrl)

            val direction = HomeViewPager2FragmentDirections.actionViewPager2FragmentToPlayingFragment(selectedPlaylist)
            view.findNavController().navigate(direction)
        }

        // 데이터를 ViewHolder 에 채워 넣음.
        fun bind(item : Playlist) {
            binding.apply {
                playlist = item
                executePendingBindings()
            }
        }
    }

    enum class FragmentType {
        SEARCH, SAVED
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

@Parcelize
data class SelectedPlaylist(
    val playlistId: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?
) : Parcelable