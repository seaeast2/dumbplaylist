package com.example.dumbplaylist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dumbplaylist.MainActivity
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import java.lang.Exception


class SavedPlaylistFragment : Fragment() {
    // ViewModel 은 공유한다.
//    private val viewModel: PlaylistsViewModel by viewModels {
//        Injector.providePlaylistViewModelFactory(requireContext())
//    }
    private lateinit var viewModel: PlaylistsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // shared ViewModel
        viewModel = (activity as MainActivity).viewModel
    }
}
