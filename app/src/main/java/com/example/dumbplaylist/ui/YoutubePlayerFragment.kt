package com.example.dumbplaylist.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.dumbplaylist.R
import com.example.dumbplaylist.databinding.YoutubePlayerFragmentBinding
import com.example.dumbplaylist.model.PlaylistRepository
import com.example.dumbplaylist.util.FullScreenHelper
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.YoutubePlayerViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem



class YoutubePlayerFragment : Fragment() {
    private lateinit var youTubePlayerView: YouTubePlayerView
    private val fullScreenHelper: FullScreenHelper by lazy {
        FullScreenHelper(requireActivity())
    }

    private val args: YoutubePlayerFragmentArgs by navArgs()
    private val viewModel: YoutubePlayerViewModel by viewModels {
        Injector.provideYoutubePlayerViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Fragment binding
        val binding =
            YoutubePlayerFragmentBinding.inflate(inflater, container, false)
        context?:binding.root

        // 2. get observe youtube player instance
        youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        // 3. set videoId to youtube player
        //args.videoId
        initYoutubePlayerView()
        return binding.root
    }

    private fun initPlayerMenu() {
        youTubePlayerView.getPlayerUiController().showMenuButton(true)
            .getMenu()?.
                addItem(MenuItem("menu item1", R.drawable.ic_android_black_24dp,
                    View.OnClickListener { view: View? ->
                        Toast.makeText(requireContext(),"item1 clicked",Toast.LENGTH_SHORT).show()}))?.
                addItem(MenuItem("menu item2", R.drawable.ic_mood_black_24dp,
                    View.OnClickListener { view: View? ->
                        Toast.makeText(requireContext(),"item2 clicked",Toast.LENGTH_SHORT).show()}))?.
                addItem(MenuItem("menu item no icon", null, View.OnClickListener { view: View? ->
                        Toast.makeText(requireContext(),"item no icon clicked", Toast.LENGTH_SHORT).show()}))
    }

    private fun initYoutubePlayerView() {
        initPlayerMenu()

        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadOrCueVideo(lifecycle, args.videoId, 0f)

                addFullScreenListenerToPlayer()
                setPlayNextVideoButtonClickListener(youTubePlayer)
            }
        })
    }

    private fun addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullScreenListener(object: YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onYouTubePlayerExitFullScreen() {

            }
        })
    }

    private fun setPlayNextVideoButtonClickListener(youTubePlayer: YouTubePlayer) {

    }
}

class YoutubePlayerViewModelFactory(
    private val repository: PlaylistRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = YoutubePlayerViewModel(repository) as T
}
