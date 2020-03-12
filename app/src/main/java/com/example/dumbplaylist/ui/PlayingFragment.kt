package com.example.dumbplaylist.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.dumbplaylist.R
import com.example.dumbplaylist.adapter.VideoListAdapter
import com.example.dumbplaylist.databinding.FragmentPlayingBinding
import com.example.dumbplaylist.util.FullScreenHelper
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PlayingFragment : Fragment() {
    // Recieve argument through navagation.
    private val args: PlayingFragmentArgs by navArgs()

    // ViewModel 은 공유한다.
    private val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(requireContext())
    }

    private val fullScreenHelper: FullScreenHelper by lazy {
        FullScreenHelper(requireActivity())
    }

    private lateinit var fragmentBinding: FragmentPlayingBinding
    private lateinit var mYouTubePlayerView: YouTubePlayerView
    private var mYouTubePlayer: YouTubePlayer? = null
    private var mPlayerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Fragment binding
        fragmentBinding = FragmentPlayingBinding.inflate(inflater, container, false)
        // 2. context 가 이미 존재 하면 그냥 리턴
        context ?: return fragmentBinding.root
        // 3. RecyclerView Adapter 생성
        val adapter = VideoListAdapter { videoId ->
            viewModel.setCurVideoId(videoId)
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
                    viewModel.loadMorePlaylistItem()
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
        if (args.playlistId != "none") {
            viewModel.fetchPlaylistItems(args.playlistId)
        }

        // 9. get observe youtube player instance
        mYouTubePlayerView = fragmentBinding.youtubePlayerView

        // 10. youtube player ui initialize
        initYoutubePlayerView()

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
        viewModel.playlistItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)

            if (mPlayerState == PlayerConstants.PlayerState.ENDED) {
                viewModel.getCurVideoId()?.let {
                    mYouTubePlayer?.loadOrCueVideo(lifecycle, it, viewModel.curPlayInfo.videoSec)
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
                if (viewModel.curPlayInfo.isFullScreen) {
                    // continue play when user enters fullscreen mode
                    viewModel.curPlayInfo.isFullScreen = false
                    viewModel.getCurVideoId()?.let {
                        youTubePlayer.loadOrCueVideo(lifecycle, it, viewModel.curPlayInfo.videoSec)
                    }
                }
                else {
                    viewModel.getCurVideoId()?.let {
                        youTubePlayer.loadOrCueVideo(lifecycle, it, 0f)
                    }
                }
                //setPlayNextVideoButtonClickListener(youTubePlayer)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                // backup second for fullscreen change
                viewModel.curPlayInfo.videoSec = second
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)

                mPlayerState = state

                if (state == PlayerConstants.PlayerState.ENDED) {
                    viewModel.getNextVideoId()?.let {
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
                viewModel.curPlayInfo.isFullScreen = true
            }

            override fun onYouTubePlayerExitFullScreen() {
                // 1. 화면 세로로 변경
                //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                // 2. 세로 화면에 맞추어 UI 변경
                fullScreenHelper.exitFullScreen()
                // 3. 사용자 버튼 감추기
                removeCustomActionsFromPlayer()
                viewModel.curPlayInfo.isFullScreen = true
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
}

