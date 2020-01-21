package com.example.dumbplaylist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dumbplaylist.databinding.YoutubePlayerFragmentBinding
import com.example.dumbplaylist.model.PlaylistRepository
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.YoutubePlayerViewModel

class YoutubePlayerFragment : Fragment() {
    private val viewModel: YoutubePlayerViewModel by viewModels {
        Injector.provideYoutubePlayerViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = YoutubePlayerFragmentBinding.inflate(inflater, container, false)
        context?:binding.root

        val youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        return binding.root
    }
}

class YoutubePlayerViewModelFactory(
    private val repository: PlaylistRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = YoutubePlayerViewModel(repository) as T
}
