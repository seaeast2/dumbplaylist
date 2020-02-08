package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.*
import com.example.dumbplaylist.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    //private val GOOGLE_YOUTUBE_API_KEY: String = "AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A"

    // LiveDatas
    val playlists = repository.playlists
    val playlistItems = repository.playlistItems

    // video control values
    private var currentPlayInfo = PlayInfo(0, 0f, false)
    val curPlayInfo = currentPlayInfo


    // Spiner 를 위한 변수
    private var mSpiner = MutableLiveData<Boolean>(false)
    // Snackbar 를 위한 문자열
    private var mSnackbarMsg = MutableLiveData<String?>()


    // playlists fetch functions ========================
    fun searchPlaylists(searchQuery: String, pageToken: String = "") {
        launchDataLoad {
            repository.tryUpdatePlaylistsCache(searchQuery, pageToken)
        }
    }
    fun clearPlaylists() {
        launchDataLoad {
            repository.clearPlaylists()
        }
    }
    fun loadMorePlaylists() {
        if (repository.playlistInfo.totalResult == 0)
            return

        if (playlistItems.value?.size?:0 < repository.playlistInfo.totalResult) {
            searchPlaylists(repository.playlistInfo.id, repository.playlistInfo.nextPageToken)
        }
    }
    fun resetPlaylistsInfo() {
        repository.playlistInfo = PlaylistsInfo("", 0, 0, "")
    }

    // PlaylistItems fetch functions ========================
    fun fetchPlaylistItems(playlistId: String, pageToken: String? = null) {
        var test = 10
        if (test < 20)
            test = 20

        launchDataLoad {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
        }
    }
    fun clearPlaylistItems() {
        launchDataLoad {
            repository.clearPlaylistItems()
        }
    }
    fun loadMorePlaylistItem() {
        if (repository.playlistItemInfo.totalResult == 0)
            return

        if (playlistItems.value?.size?:0 < repository.playlistItemInfo.totalResult) {
            fetchPlaylistItems(repository.playlistItemInfo.id, repository.playlistItemInfo.nextPageToken)
        }
    }
    fun resetPlaylistItemsInfo() {
        repository.playlistItemInfo = PlaylistsInfo("", 0, 0, "")
    }

    // Youtube player handle functions =====================
    fun getCurVideoId() : String? = playlistItems.value?.get(currentPlayInfo.videoIndex)?.id

    fun getNextVideoId(): String? {
        currentPlayInfo.videoSec = 0f

        if (currentPlayInfo.videoIndex < playlistItems.value?.size ?: 0)
            currentPlayInfo.videoIndex++
        else
            currentPlayInfo.reset()

        return playlistItems.value?.get(currentPlayInfo.videoIndex)?.id
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

data class PlayInfo (
    var videoIndex: Int,
    var videoSec: Float,
    var isFullScreen:Boolean) {
    fun reset() {
        videoIndex = 0
        videoSec = 0f
    }
}

