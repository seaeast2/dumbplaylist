package com.example.dumbplaylist.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.VideoListAdapter
import com.example.dumbplaylist.databinding.VideoListFragmentBinding
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel

class PlaylistItemsFragment : Fragment() {
    // Recieve argument through navagation.
    private val args: PlaylistItemsFragmentArgs by navArgs()


    // ViewModel 은 공유한다.
    private val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Fragment binding
        val binding = VideoListFragmentBinding.inflate(inflater, container, false)
        // 2. context 가 이미 존재 하면 그냥 리턴
        context ?: return binding.root
        // 3. RecyclerView Adapter 생성
        val adapter = VideoListAdapter()
        // 4. binding 과 recyclerview adapter 연결
        binding.videolistRcview.adapter = adapter
        // 5. ViewModel 과 RecyclerView Adapter 를 연결하여 감시하도록 함
        subscribeUi(adapter)
        // 6. 메뉴 활성화
        setHasOptionsMenu(true)
        // 7. PlaylistItems db 를 초기화
        viewModel.clearPlaylistItems()
        // 8. args 를통해 받은 playlistId로 PlaylistItem fetch
        viewModel.fetchPlaylistItems(args.playlistId)
        return binding.root
    }

    // Menu ====================================
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.playlist_frag_menu, menu)
    }

    // 메뉴 선택 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_dummy_menu -> {
                true
            }
            R.id.del_dummy_menu -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun subscribeUi(adapter: VideoListAdapter) {
        viewModel.playlistItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}

