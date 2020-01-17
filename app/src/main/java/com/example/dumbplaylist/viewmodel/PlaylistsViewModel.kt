package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dumbplaylist.model.Playlist
import com.example.dumbplaylist.model.PlayItem
import com.example.dumbplaylist.model.PlaylistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    // TODO : 어디 구석에 아래 값들 저장해 놓을 것
    private val GOOGLE_YOUTUBE_API_KEY: String = "AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A"
    private val CHANNEL_ID: String = "UCGDA1e6qQSAH0R9hoip9VrA"
    private val CHANNLE_GET_URL: String = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + ""

    // playlist 와 playlistItem 의 참조를 연결함
    val playlists: LiveData<List<Playlist>> = repository.playlists
    val playItems: LiveData<List<PlayItem>> = repository.playItems

    // Spiner 를 위한 변수
    private var mSpiner = MutableLiveData<Boolean>(false)
    // Snackbar 를 위한 문자열
    private var mSnackbarMsg = MutableLiveData<String?>()


    // 여기서 playlists 요청
    fun fetchPlaylists() {
        launchDataLoad { repository.tryUpdateRecentPlaylistsCache() }
    }

    fun fetchPlayItems() {
        launchDataLoad { repository.tryUpdatePlayItemsCache() }
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

