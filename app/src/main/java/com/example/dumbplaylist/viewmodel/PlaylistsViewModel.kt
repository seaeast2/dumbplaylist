package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dumbplaylist.model.AppDatabase
import com.example.dumbplaylist.model.Playlist
import com.example.dumbplaylist.model.PlaylistItem
import com.example.dumbplaylist.model.PlaylistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    //private val GOOGLE_YOUTUBE_API_KEY: String = "AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A"

    // playlist 와 playlistItem 의 참조를 연결함
    val playlists: LiveData<List<Playlist>> = repository.playlists
    val playlistItems: LiveData<List<PlaylistItem>> = repository.playItems

    // Spiner 를 위한 변수
    private var mSpiner = MutableLiveData<Boolean>(false)
    // Snackbar 를 위한 문자열
    private var mSnackbarMsg = MutableLiveData<String?>()


    // 여기서 playlists 관련 함수들 ========================
    fun searchPlaylists(searchQuery: String, pageToken: String) {
        launchDataLoad {
            repository.tryUpdatePlaylistsCache(searchQuery, pageToken)
        }
    }
    fun clearPlaylists() {
        launchDataLoad {
            repository.clearPlaylists()
        }
    }

    // PlaylistItems 관련 함수들 ========================
    fun fetchPlaylistItems(playlistId: String, pageToken: String) {
        launchDataLoad {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
        }
    }
    fun clearPlaylistItems() {
        launchDataLoad {
            repository.clearPlaylistItems()
        }
    }

    // Coroutine 호출 헬퍼 함수
    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                mSpiner.value = true
                block()
            } catch(error: Throwable) {
                mSnackbarMsg.value = error.message
            } finally {
                mSpiner.value = false
            }
        }
    }
}

