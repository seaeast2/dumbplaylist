package com.example.dumbplaylist.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.MainActivity
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.SelectedPlaylist
import com.example.dumbplaylist.adapter.VideoListAdapter
import com.example.dumbplaylist.databinding.FragmentPlayingBinding
import com.example.dumbplaylist.model.Playlist
import com.example.dumbplaylist.model.SavedPlaylist
import com.example.dumbplaylist.util.FullScreenHelper
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.lang.Exception

class PlayingFragment : Fragment() {
    // Recieve argument through navagation.
    private val args: PlayingFragmentArgs by navArgs()

    private lateinit var mViewModel: PlaylistsViewModel
    private val mFullScreenHelper: FullScreenHelper by lazy {
        FullScreenHelper(requireActivity())
    }

    private lateinit var mFragmentBinding: FragmentPlayingBinding
    private lateinit var mYouTubePlayerView: YouTubePlayerView
    private var mYouTubePlayer: YouTubePlayer? = null
    private var mPlayerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get Shared ViewModel
        mViewModel = (activity as MainActivity).viewModel

        // Fragment binding
        mFragmentBinding = DataBindingUtil.inflate<FragmentPlayingBinding>(inflater,
                R.layout.fragment_playing, container, false
        ).apply {
            viewModel = mViewModel
            lifecycleOwner = viewLifecycleOwner // In case binding has LiveData, lifeCycleOwner is mandatory.

            fabCallback = object : FabCallback {
                override fun add(selectedPlaylist: SelectedPlaylist?) {
                    selectedPlaylist?.let {playlist ->
                        fab?.let {
                            hideFab(it)
                            mViewModel.addSavedPlaylist(playlist)
                            Snackbar.make(root, "Add playlist to saved list", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        context ?: return mFragmentBinding.root

        val adapter = VideoListAdapter { videoId ->
            mViewModel.setCurVideoId(videoId)
            mYouTubePlayer?.loadOrCueVideo(lifecycle, videoId, 0f)
        }

        initRecyclerView(adapter)

        initActionBar()

        initSelectedPlaylist(args.selectedPlaylist)

        // youtube player ui initialization
        initYoutubePlayerView()

        // observing playlist item and update recyclerview when new items are added.
        subscribeUi(adapter)

        return mFragmentBinding.root
    }

    private fun initActionBar() {
        // hide action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun initRecyclerView(adapter: VideoListAdapter) {
        // set up recyclerview
        mFragmentBinding.videolistRcview?.let {
            it.adapter = adapter

            // 4.2 무한 스크롤을 위해 ScrollListener 등록
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // 더 아래로 스크롤 할수 있는지 검사 후 스크롤 불가면 데이터 추가 로딩
                    if (!recyclerView.canScrollVertically(1)) {
                        mViewModel.loadMorePlaylistItem()
                    }
                }
            })
        }
    }

    private fun initSelectedPlaylist(selectedPlaylist: SelectedPlaylist?) {
        // fetch selected playlist items from youtube api.
        mViewModel.selectedPlaylist = selectedPlaylist
        if (mViewModel.selectedPlaylist != null) { // we have new playlist
            mViewModel.selectedPlaylist?.let {
                mViewModel.fetchPlaylistItems(it.playlistId)
                saveSelectedPlaylist()
            }
        }
        else {
            // if we don't have new playlist, restore old playlist
            mViewModel.selectedPlaylist = restoreSelectedPlaylist()
        }
    }



    // Menu ====================================
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.playlist_frag_menu, menu)
    }

    // 메뉴 선택 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
//            R.id.add_dummy_menu -> {
//                viewModel.loadMorePlaylistItem()
//                true
//            }
//            R.id.del_dummy_menu -> {
//                true
//            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeUi(adapter: VideoListAdapter) {
        mViewModel.playlistItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)

            if (mPlayerState == PlayerConstants.PlayerState.ENDED) {
                mViewModel.getCurVideoId()?.let {
                    mYouTubePlayer?.loadOrCueVideo(lifecycle, it, mViewModel.curPlayInfo.videoSec)
                }
            }
        }

        mViewModel.savedlists.observe(viewLifecycleOwner) {
            initFab(it)
        }
    }

    private fun initFab(list : List<SavedPlaylist>) {
        list.find { savedlist ->
            savedlist.playlistId == mViewModel.selectedPlaylist?.playlistId
        }?.let {
            mFragmentBinding.fab?.let {fab ->
                hideFab(fab)
            }
        }
    }

    // Youtube player initialize functions ====================================
    private fun initYoutubePlayerView() {
        // get observe youtube player instance
        mYouTubePlayerView = mFragmentBinding.youtubePlayerView

        initPlayerMenu()
        addYoutubeListener()
        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        lifecycle.addObserver(mYouTubePlayerView)
        addFullScreenListenerToPlayer()
    }

    private fun addYoutubeListener() {
        mYouTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                mYouTubePlayer = youTubePlayer
                if (mViewModel.curPlayInfo.isFullScreen) {
                    // continue play when user enters fullscreen mode
                    mViewModel.curPlayInfo.isFullScreen = false
                    mViewModel.getCurVideoId()?.let {
                        youTubePlayer.loadOrCueVideo(lifecycle, it, mViewModel.curPlayInfo.videoSec)
                    }
                }
                else {
                    mViewModel.getCurVideoId()?.let {
                        youTubePlayer.loadOrCueVideo(lifecycle, it, 0f)
                    }
                }
                //setPlayNextVideoButtonClickListener(youTubePlayer)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                // backup second for fullscreen change
                mViewModel.curPlayInfo.videoSec = second
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)

                mPlayerState = state

                if (state == PlayerConstants.PlayerState.ENDED) {
                    mViewModel.getNextVideoId()?.let {
                        youTubePlayer.loadOrCueVideo(lifecycle, it, 0f)
                    }
                }
            }
        })
    }

    private fun initPlayerMenu() {
        mYouTubePlayerView.getPlayerUiController().showMenuButton(true)
            .getMenu()?.
                addItem(
                    com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem("menu item1",
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
            override fun onYouTubePlayerEnterFullScreen() {
                // 1. 화면 가로로 변경
                //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                // 2. 가로화면에 맞추어 UI 변경
                mFullScreenHelper.enterFullScreen()
                // 3. 사용자 버튼 추가
                addCustomActionsToPlayer()
                mViewModel.curPlayInfo.isFullScreen = true
            }

            override fun onYouTubePlayerExitFullScreen() {
                // 1. 화면 세로로 변경
                //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                // 2. 세로 화면에 맞추어 UI 변경
                mFullScreenHelper.exitFullScreen()
                // 3. 사용자 버튼 감추기
                removeCustomActionsFromPlayer()
                mViewModel.curPlayInfo.isFullScreen = true
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
                    Toast.makeText(requireContext(),"custom action1 clicked",Toast.LENGTH_SHORT).show()
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
            putString("playlistId", mViewModel.selectedPlaylist?.playlistId)
            putString("title", mViewModel.selectedPlaylist?.title)
            putString("description", mViewModel.selectedPlaylist?.description)
            putString("thumbnailUrl", mViewModel.selectedPlaylist?.thumbnailUrl)
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
}

