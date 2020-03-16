package com.example.dumbplaylist.ui

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.MainActivity
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.PlaylistAdapter
import com.example.dumbplaylist.databinding.FragmentSearchedPlaylistsBinding
import com.example.dumbplaylist.model.PlaylistRepository
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import java.lang.Exception


class SearchedPlaylistsFragment : Fragment() {
    // viewModel 은 observe 되기 전에 항상 생성되어 있어야 함.
    // 그래서 class 생성시 초기화 되도록 property delegation 으로 처리
//    private val viewModel: PlaylistsViewModel by viewModels {
//        Injector.providePlaylistViewModelFactory(requireContext())
//    }
    private lateinit var viewModel: PlaylistsViewModel
    private lateinit var mBinding: FragmentSearchedPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {
        mBinding = FragmentSearchedPlaylistsBinding.inflate(inflater, container, false)
        context ?: return mBinding.root

        // shared ViewModel
        viewModel = (activity as MainActivity).viewModel

        val adapter = PlaylistAdapter(PlaylistAdapter.FragmentType.SEARCH)
        // binding 을 사용해서 RecyclerView.Adapter 를 연결함
        mBinding.playlistsRcview.adapter = adapter
        mBinding.playlistsRcview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMorePlaylists()
                }
            }
        })

        subscribeUi(adapter) // RecyclerView.Adapter 에 데이터 연결
        initSearch()

        // Fragment용 메뉴활성화
        setHasOptionsMenu(true)

        return mBinding.root
    }

    //
    private fun initSearch() {
        mBinding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedSearchFromInput()
                true
            } else {
                false
            }
        }

        mBinding.input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedSearchFromInput()
                true
            }
            else {
                false
            }
        }
    }

    private fun updatedSearchFromInput() {
        mBinding.input.text.trim().toString().let {
            if (it.isNotEmpty()) {
                viewModel.searchPlaylists(it)
                //mBinding.playlistsRcview.scrollToPosition(0)
            }
        }
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
//            R.id.add_dummy_menu -> {
//                updateData()
//                true
//            }
//            R.id.del_dummy_menu -> {
//                true
//            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val TAG = "PlaylistsFragment"
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

