package com.example.dumbplaylist.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private val fullScreenHelper: FullScreenHelper by lazy {
        FullScreenHelper(requireActivity())
    }

    private lateinit var fragmentBinding: FragmentPlayingBinding
    private lateinit var mYouTubePlayerView: YouTubePlayerView
    private var mYouTubePlayer: YouTubePlayer? = null
    private var mPlayerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get Shared ViewModel
        mViewModel = (activity as MainActivity).viewModel

        // Fragment binding
        fragmentBinding =
            DataBindingUtil.inflate<FragmentPlayingBinding>(inflater,
                R.layout.fragment_playing, container, false).apply {
                viewModel = mViewModel
                fabCallback = object : FabCallback {
                    override fun add(selectedPlaylist: SelectedPlaylist?) {
                        selectedPlaylist?.let {
                            // hide fab
                            //hideFab()
                            mViewModel.addSavedPlaylist()
                            Snackbar.make(root, "Add playlist to saved list", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }

        // context 가 이미 존재 하면 그냥 리턴
        context ?: return fragmentBinding.root

        // RecyclerView Adapter 생성
        val adapter = VideoListAdapter { videoId ->
            mViewModel.setCurVideoId(videoId)
            mYouTubePlayer?.loadOrCueVideo(lifecycle, videoId, 0f)
        }
        // 4.1 binding 과 recyclerview adapter 연결
        fragmentBinding.videolistRcview.adapter = adapter
        // 4.2 무한 스크롤을 위해 ScrollListener 등록
        fragmentBinding.videolistRcview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 더 아래로 스크롤 할수 있는지 검사 후 스크롤 불가면 데이터 추가 로딩
                if (!recyclerView.canScrollVertically(1)) {
                    mViewModel.loadMorePlaylistItem()
                }
            }
        })

        // 5. ViewModel 과 RecyclerView Adapter 를 연결하여 감시하도록 함
        subscribeUi(adapter)
        // 6. 메뉴 활성화
        //setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.hide()

        // 7. PlaylistItems db 를 초기화
        //viewModel.clearPlaylistItems()
        //viewModel.resetPlaylistItemsInfo()

        // 8. args 를통해 받은 playlistId로 PlaylistItem fetch
        if (args.selectedPlaylist != null) { // playlist 가 있으면 fetching 하고, SharePreference 에 저장
            mViewModel.selectedPlaylist = args.selectedPlaylist // set current playlist
            mViewModel.selectedPlaylist?.let {
                mViewModel.fetchPlaylistItems(it.playlistId)
            }
            saveSelectedPlaylist()
        }
        else {
            // playlist 가 null 이면 SharedPreference 읽어옴
            mViewModel.selectedPlaylist = restoreSelectedPlaylist()
        }
        //Toast.makeText(requireContext(), viewModel.curPlaylistId, Toast.LENGTH_SHORT).show()

        // 9. get observe youtube player instance
        mYouTubePlayerView = fragmentBinding.youtubePlayerView

        // 10. youtube player ui initialize
        initYoutubePlayerView()

        // 11. set up add list button click listener
        fragmentBinding.fab.setOnClickListener {view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        return fragmentBinding.root
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

    fun subscribeUi(adapter: VideoListAdapter) {
        mViewModel.playlistItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)

            if (mPlayerState == PlayerConstants.PlayerState.ENDED) {
                mViewModel.getCurVideoId()?.let {
                    mYouTubePlayer?.loadOrCueVideo(lifecycle, it, mViewModel.curPlayInfo.videoSec)
                }
            }
        }
    }

    // Youtube player initialize functions ====================================
    private fun initYoutubePlayerView() {
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
                fullScreenHelper.enterFullScreen()
                // 3. 사용자 버튼 추가
                addCustomActionsToPlayer()
                mViewModel.curPlayInfo.isFullScreen = true
            }

            override fun onYouTubePlayerExitFullScreen() {
                // 1. 화면 세로로 변경
                //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                // 2. 세로 화면에 맞추어 UI 변경
                fullScreenHelper.exitFullScreen()
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

    private fun hideFab(fab: FloatingActionButton) {
        fab.hide()
    }

    // FloatingActionButton Callback
    interface FabCallback {
        fun add(selectedPlaylist: SelectedPlaylist?)
    }
}

