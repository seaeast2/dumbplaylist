package com.example.dumbplaylist.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.PlaylistAdapter
import com.example.dumbplaylist.databinding.PlaylistsFragmentBinding
import com.example.dumbplaylist.model.PlaylistRepository
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel


class PlaylistsFragment : Fragment() {
    // viewModel 은 observe 되기 전에 항상 생성되어 있어야 함.
    // 그래서 class 생성시 초기화 되도록 property delegation 으로 처리
    private val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {
        val binding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = PlaylistAdapter()
        // binding 을 사용해서 RecyclerView.Adapter 를 연결함
        binding.playlistsRcview.adapter = adapter
        binding.playlistsRcview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMorePlaylists()
                }
            }
        })


        subscribeUi(adapter) // RecyclerView.Adapter 에 데이터 연결

        // Fragment용 메뉴활성화
        setHasOptionsMenu(true)

        // TODO : 테스트용으로 삭제
        //viewModel.clearPlaylists()
        //viewModel.resetPlaylistsInfo()
        return binding.root
    }

    private fun subscribeUi(adapter: PlaylistAdapter) {
        viewModel.playlists.observe(viewLifecycleOwner) {playlists ->
            adapter.submitList(playlists)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.playlist_frag_menu, menu)
    }

    // 메뉴 선택 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_dummy_menu -> {
                updateData()
                true
            }
            R.id.del_dummy_menu -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updateData() {
        viewModel.searchPlaylists("twice")
    }

    companion object {
        private val TAG = "PlaylistFragment"
    }
}

/**
 * Factory for creating a [PlaylistsViewModel] with a constructor that takes a [PlaylistRepository].
 */

class MyPlaylistViewModelFactory(
    private val repository: PlaylistRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = PlaylistsViewModel(repository) as T
}