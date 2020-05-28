package com.seaeast22.dumbplaylist.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.seaeast22.dumbplaylist.MainActivity
import com.seaeast22.dumbplaylist.R
import com.seaeast22.dumbplaylist.adapter.PlaylistAdapter
import com.seaeast22.dumbplaylist.databinding.FragmentSearchedPlaylistsBinding
import com.seaeast22.dumbplaylist.model.PlaylistRepository
import com.seaeast22.dumbplaylist.viewmodel.PlaylistsViewModel
import com.seaeast22.dumbplaylist.viewmodel.SEARCH_FRAGMENT


class SearchedPlaylistsFragment : Fragment() {
    private lateinit var mViewModel: PlaylistsViewModel
    private lateinit var mFragmentBinding: FragmentSearchedPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {
        mFragmentBinding = FragmentSearchedPlaylistsBinding.inflate(inflater, container, false)
        context ?: return mFragmentBinding.root

        // shared ViewModel
        mViewModel = (activity as MainActivity).viewModel
        mViewModel.currentFragmentType = SEARCH_FRAGMENT

        val adapter = PlaylistAdapter()
        initRecyclerView(adapter)
        subscribeUi(adapter) // RecyclerView.Adapter 에 데이터 연결

        //initActionBar()
        // Fragment용 메뉴활성화
        setHasOptionsMenu(true)
        return mFragmentBinding.root
    }

    /*override fun onStart() {
        super.onStart()

       // bottom navigation 초기화
        val mainActivity = activity as MainActivity
        if (!mainActivity.mBinding.bottomNav.isShown) {
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            mainActivity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            mainActivity.mBinding.bottomNav.visibility = View.VISIBLE
            mainActivity.mBinding.bottomNav.invalidate()
            Log.d(TAG, "turn on boottomNav visible")
        }
    }*/

    private fun initRecyclerView(adapter: PlaylistAdapter) {
        // binding 을 사용해서 RecyclerView.Adapter 를 연결함
        mFragmentBinding.playlistsRcview.adapter = adapter
        // Set auto loading at lower bounding
        mFragmentBinding.playlistsRcview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    mViewModel.playlistInfo.lastItem?.let {playlist ->
                        mViewModel.loadMorePlaylists(playlist.playlistId, playlist.pageToken)
                    }
                }
            }
        })
    }

    private fun initActionBar() {
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun subscribeUi(adapter: PlaylistAdapter) {
        mViewModel.playlists.observe(viewLifecycleOwner) {playlists ->
            adapter.submitList(playlists)

            // Updates playlist info
            if (playlists.isNotEmpty())
                mViewModel.playlistInfo = PlaylistsViewModel.PlaylistInfo(playlists.size, playlists.last())
            else
                mViewModel.playlistInfo = PlaylistsViewModel.PlaylistInfo()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.search_toolbar)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mViewModel.searchPlaylists(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    companion object {
        private val TAG = "SearchedPlaylists"
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

