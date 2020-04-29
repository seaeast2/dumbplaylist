package com.example.dumbplaylist.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.MainActivity
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.PlaylistAdapter
import com.example.dumbplaylist.databinding.FragmentSavedPlaylistBinding
import com.example.dumbplaylist.util.SwipeToRemoveCallback
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel


class SavedPlaylistFragment : Fragment() {
    // ViewModel 은 공유한다.
    private lateinit var mViewModel: PlaylistsViewModel
    private lateinit var mFragmentBinding: FragmentSavedPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = (activity as MainActivity).viewModel

        mFragmentBinding = DataBindingUtil.inflate<FragmentSavedPlaylistBinding>(inflater,
            R.layout.fragment_saved_playlist, container, false)

        context ?: return mFragmentBinding.root

        val adapter = PlaylistAdapter(PlaylistAdapter.FragmentType.SAVED)
        initRecyclerView(adapter)
        subscribeUi(adapter)
        initActionBar()

        val mainActivity = activity as MainActivity
        if (!mainActivity.mBinding.bottomNav.isShown) {
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            mainActivity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE )
            mainActivity.mBinding.bottomNav.visibility = View.VISIBLE
            mainActivity.mBinding.bottomNav.invalidate()
            Log.d(TAG, "turn on boottomNav visible")
        }

        return mFragmentBinding.root
    }

    private fun initActionBar() {
        //setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.saved_list_menu, menu)
//    }

    private fun initRecyclerView(adapter: PlaylistAdapter) {
        mFragmentBinding.savedList.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToRemoveCallback(mViewModel, context))
        itemTouchHelper.attachToRecyclerView(mFragmentBinding.savedList)
    }

    private fun subscribeUi(adapter: PlaylistAdapter) {
        mViewModel.savedlists.observe(viewLifecycleOwner) {
            adapter.submitSavedList(it)
        }
    }

    companion object {
        private val TAG = "SavedPlaylistFragment"
    }
}
