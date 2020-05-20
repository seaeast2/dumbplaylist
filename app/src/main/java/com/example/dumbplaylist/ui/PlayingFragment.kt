package com.example.dumbplaylist.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.MainActivity
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.SelectedPlaylist
import com.example.dumbplaylist.adapter.VideoListAdapter
import com.example.dumbplaylist.databinding.FragmentPlayingBinding
import com.example.dumbplaylist.generated.callback.OnClickListener
import com.example.dumbplaylist.model.SavedPlaylist
import com.example.dumbplaylist.util.FullScreenHelper
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlayingViewModel
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PlayingFragment : Fragment() {
    // Recieve argument through navagation.
    private val args: PlayingFragmentArgs by navArgs()

    private lateinit var mSharedViewModel: PlaylistsViewModel
    private val mPlayingViewModel: PlayingViewModel by viewModels { 
        Injector.providePlayingViewModelFactory(requireContext())
    }
    private lateinit var mFragmentBinding: FragmentPlayingBinding
    private lateinit var mYouTubePlayerView: YouTubePlayerView
    private var mYouTubePlayer: YouTubePlayer? = null
    private var mPlayerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
    private lateinit var mAdapter : VideoListAdapter
    private var mFabShowStatus: Boolean = true
    private val mFullScreenHelper: FullScreenHelper by lazy {
        FullScreenHelper(requireActivity())
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        Log.d(TAG, "onCreateView Before inflation")
        // Get Shared ViewModel
        mSharedViewModel = (activity as MainActivity).viewModel

        // Fragment binding
        mFragmentBinding = DataBindingUtil.inflate<FragmentPlayingBinding>(inflater,
                R.layout.fragment_playing, container, false
        ).apply {
            viewModel = mSharedViewModel
            lifecycleOwner = viewLifecycleOwner // In case binding has LiveData, lifeCycleOwner is mandatory.

            fabCallback = object : FabCallback {
                override fun add(selectedPlaylist: SelectedPlaylist?) {
                    selectedPlaylist?.let {playlist ->
                        fab?.let {
                            hideFab(it)
                            mSharedViewModel.addSavedPlaylist(playlist)
                            Snackbar.make(root, "Add playlist to saved list", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        context ?: return mFragmentBinding.root

        Log.d(TAG, "onCreateView After inflation")

        mAdapter = VideoListAdapter {  videoId ->
            if (mPlayingViewModel.getCurVideoId() != videoId) {
                mPlayingViewModel.setCurVideoId(videoId)
                mYouTubePlayer?.loadOrCueVideo(lifecycle, videoId, 0f)
            }
        }

        initRecyclerView(mAdapter)

        initActionBar()

        initSelectedPlaylist(args.selectedPlaylist)

        // youtube player ui initialization
        initYoutubePlayerView()

        // observing playlist item and update recyclerview when new items are added.
        subscribeUi(mAdapter)



        return mFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        if (mPlayingViewModel.currentPlayInfo.isFullScreen)
            mYouTubePlayerView.enterFullScreen()
    }

    private fun initActionBar() {
        // hide action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun initRecyclerView(adapter: VideoListAdapter) {
        // set up recyclerview
        mFragmentBinding.videolistRcview?.let {rcview ->
            rcview.adapter = adapter

            // 4.2 무한 스크롤을 위해 ScrollListener 등록
            rcview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // 더 아래로 스크롤 할수 있는지 검사 후 스크롤 불가면 데이터 추가 로딩
                    if (!recyclerView.canScrollVertically(1)) {
                        mPlayingViewModel.playlistItemInfo.lastItem?.let {playlistitem ->
                            mSharedViewModel.loadMorePlaylistItem(playlistitem.playlistId,
                                playlistitem.pageToken)
                        }

                    }
                }
            })
        }
    }

    private fun initSelectedPlaylist(selectedPlaylist: SelectedPlaylist?) {
        // fetch selected playlist items from youtube api.
        selectedPlaylist?.let {
            mSharedViewModel.selectedPlaylist = it
            mSharedViewModel.fetchPlaylistItems(it.playlistId)
            saveSelectedPlaylist()
        }
        if ( mSharedViewModel.selectedPlaylist == null) {
            mSharedViewModel.selectedPlaylist = restoreSelectedPlaylist()
        }
    }

    private fun subscribeUi(adapter: VideoListAdapter) {
        mSharedViewModel.playlistItems.observe(viewLifecycleOwner) {
            // Convert raw data to display data
            mPlayingViewModel.videoList = it.map {
                PlayingViewModel.VideoItem(it.id, it.title, it.description, it.thumbnailUrl, false)
            }
            adapter.submitList(mPlayingViewModel.videoList)

            if (mPlayerState == PlayerConstants.PlayerState.ENDED) {
                mPlayingViewModel.getCurVideoId()?.let {
                    mYouTubePlayer?.loadOrCueVideo(lifecycle, it, mPlayingViewModel.currentPlayInfo.videoSec)
                    mAdapter.setSelectedPos(mPlayingViewModel.currentPlayInfo.videoPosition)
                }
            }

            // update playlistitem info
            if (it.isNotEmpty())
                mPlayingViewModel.playlistItemInfo = PlayingViewModel.PlaylistItemInfo(it.size, it.last())
            else
                mPlayingViewModel.playlistItemInfo = PlayingViewModel.PlaylistItemInfo()
        }

        mSharedViewModel.savedlists.observe(viewLifecycleOwner) {
            initFab(it)
        }
    }

    private fun initFab(list : List<SavedPlaylist>) {
        list.find { savedlist ->
            savedlist.playlistId == mSharedViewModel.selectedPlaylist?.playlistId
        }?.let {
            mFragmentBinding.fab?.let {fab ->
                hideFab(fab)
                mFabShowStatus = false
            }
        }
    }

    private fun selectItemAndMove(position: Int) {
        mAdapter.setSelectedPos(position)
        mFragmentBinding.videolistRcview.smoothScrollToPosition(position)
    }


    // Youtube player initialize functions ====================================
    private fun initYoutubePlayerView() {
        // get observe youtube player instance
        mYouTubePlayerView = mFragmentBinding.youtubePlayerView

        //initPlayerMenu()
        addYoutubeListener()
        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        lifecycle.addObserver(mYouTubePlayerView)
        addFullScreenListenerToPlayer()
    }

    private fun forceHideStatusBar() {
        activity?.let {
            val decorView = it.window.decorView
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun addYoutubeListener() {
        mYouTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                mYouTubePlayer = youTubePlayer
                mPlayingViewModel.getCurVideoId()?.let {
                    youTubePlayer.loadOrCueVideo(lifecycle, it, mPlayingViewModel.currentPlayInfo.videoSec)
                    selectItemAndMove(mPlayingViewModel.currentPlayInfo.videoPosition)
                }

                if (mYouTubePlayerView.isFullScreen()) {
                    forceHideStatusBar()
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                //super.onCurrentSecond(youTubePlayer, second)
                // backup second for fullscreen change
                mPlayingViewModel.currentPlayInfo.videoSec = second
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                //super.onStateChange(youTubePlayer, state)
                mPlayerState = state

                if (state == PlayerConstants.PlayerState.ENDED) {
                    if (mPlayingViewModel.getNextVideoId().isNullOrEmpty()) {
                        mPlayingViewModel.playlistItemInfo.lastItem?.let {
                            mSharedViewModel.loadMorePlaylistItem(it.playlistId, it.pageToken)
                        }
                    }
                    else {
                        youTubePlayer.loadOrCueVideo(lifecycle, mPlayingViewModel.getCurVideoId()!!, 0f)
                        selectItemAndMove(mPlayingViewModel.currentPlayInfo.videoPosition)
                    }
                }
            }
        })
    }

    private fun initPlayerMenu() {
        mYouTubePlayerView.getPlayerUiController().showMenuButton(true)
            .getMenu()?.
                addItem(
                    com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem("Touch block",
                        R.drawable.ic_android_black_24dp, View.OnClickListener { view: View? ->
                            Toast.makeText(requireContext(), "item1 clicked", Toast.LENGTH_SHORT).show()
                        })
                )?.
                addItem(
                    com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem("menu item2",
                        R.drawable.ic_mood_black_24dp, View.OnClickListener { view: View? ->
                            Toast.makeText(requireContext(), "item2 clicked", Toast.LENGTH_SHORT).show()
                        })
                )?.
                addItem(
                    com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem(
                        "menu item no icon",null,
                        View.OnClickListener { view: View? ->
                            Toast.makeText(requireContext(),"item no icon clicked",Toast.LENGTH_SHORT).show()
                        })
                )
    }

    private fun addFullScreenListenerToPlayer() {
        mYouTubePlayerView.addFullScreenListener(object: YouTubePlayerFullScreenListener {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onYouTubePlayerEnterFullScreen() {

                val mainActivity = activity as MainActivity
                mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                mFullScreenHelper.enterFullScreen()
                //addCustomActionsToPlayer()
                mPlayingViewModel.currentPlayInfo.isFullScreen = true
                mFragmentBinding.fab.hide()
            }

            override fun onYouTubePlayerExitFullScreen() {
                val mainActivity = activity as MainActivity
                mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                mFullScreenHelper.exitFullScreen()
                //removeCustomActionsFromPlayer()
                mPlayingViewModel.currentPlayInfo.isFullScreen = false
                if(mFabShowStatus)
                    mFragmentBinding.fab.show()
            }
        })
    }

    private fun setPlayNextVideoButtonClickListener(youTubePlayer: YouTubePlayer) {
//        val playNextVideoButton: Button =
//            findViewById<Button>(R.id.next_video_button)

//        playNextVideoButton.setOnClickListener { view: View? ->
//            youTubePlayer.loadOrCueVideo(
//                lifecycle,
//                VideoIdsProvider.getNextVideoId(),
//                0f
//            )
//        }
    }

    /**
     * This method adds a new custom action to the player.
     * Custom actions are shown next to the Play/Pause button in the middle of the player.
     */
    private fun addCustomActionsToPlayer() {
        val customAction1Icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fast_rewind_white_24dp)
        val customAction2Icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fast_forward_white_24dp)

        customAction1Icon?.let {
            mYouTubePlayerView.getPlayerUiController().setCustomAction1(it,
                View.OnClickListener { view: View? ->
                    Toast.makeText(requireContext(),"custom action2 clicked",Toast.LENGTH_SHORT).show()


                }
            )
        }

        customAction2Icon?.let {
            mYouTubePlayerView.getPlayerUiController().setCustomAction2(it,
                View.OnClickListener { view: View? ->
                    Toast.makeText(requireContext(), "custom action2 clicked", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun removeCustomActionsFromPlayer() {
        mYouTubePlayerView.getPlayerUiController().showCustomAction1(false)
        mYouTubePlayerView.getPlayerUiController().showCustomAction2(false)
    }

    private fun saveSelectedPlaylist() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("playlistId", mSharedViewModel.selectedPlaylist?.playlistId)
            putString("title", mSharedViewModel.selectedPlaylist?.title)
            putString("description", mSharedViewModel.selectedPlaylist?.description)
            putString("thumbnailUrl", mSharedViewModel.selectedPlaylist?.thumbnailUrl)
            commit()
        }
    }

    private fun restoreSelectedPlaylist() : SelectedPlaylist? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return null

        if (sharedPref.getString("playlistId", null) == null)
            return null

        return SelectedPlaylist(
            sharedPref.getString("playlistId", null)?:"",
            sharedPref.getString("title", null)?:"",
            sharedPref.getString("description", null)?:"",
            sharedPref.getString("thumbnailUrl",null)?:"")
    }

    // FloatingActionButtons anchored to AppBarLayouts have their visibility controlled by the scroll position.
    // We want to turn this behavior off to hide the FAB when it is clicked.
    //
    // This is adapted from Chris Banes' Stack Overflow answer: https://stackoverflow.com/a/41442923
    private fun hideFab(fab: FloatingActionButton) {
        fab.layoutParams?.let {param ->
            val params = param as CoordinatorLayout.LayoutParams
            params.behavior?.let {behavior ->
                val behaviors = behavior as FloatingActionButton.Behavior
                behaviors.isAutoHideEnabled = false
            }
        }

        fab.hide()
    }

    // FloatingActionButton Callback
    interface FabCallback {
        fun add(selectedPlaylist: SelectedPlaylist?)
    }

    companion object {
        private val TAG = "PlayingFragment"
    }
}


/**
 * Factory for creating a [PlaylistsViewModel] with a constructor that takes a [PlaylistRepository].
 */

class MyPlayingViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = PlayingViewModel() as T
}