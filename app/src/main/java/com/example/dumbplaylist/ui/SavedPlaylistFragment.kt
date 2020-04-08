package com.example.dumbplaylist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dumbplaylist.MainActivity
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import java.lang.Exception


class SavedPlaylistFragment : Fragment() {
    // ViewModel 은 공유한다.
    private lateinit var viewModel: PlaylistsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).viewModel

        //return super.onCreateView(inflater, container, savedInstanceState)
        return null
    }
}
