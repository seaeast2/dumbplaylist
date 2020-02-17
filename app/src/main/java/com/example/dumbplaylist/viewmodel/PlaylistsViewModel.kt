package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.*
import com.example.dumbplaylist.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    // LiveData
    val playlists = repository.playlists
    val playlistItems = repository.playlistItems

    // video control values
    private var currentPlayInfo = PlayInfo(0, 0f, false)
    val curPlayInfo = currentPlayInfo

    // laoding Spiner
    private var mSpiner = MutableLiveData<Boolean>(false)
    // error massge popup Snackbar
    private var mSnackbarMsg = MutableLiveData<String?>()


    // playlists fetch functions ========================
    fun searchPlaylists(searchQuery: String, pageToken: String? = null) {
        launchDataLoad {
            repository.tryUpdatePlaylistsCache(searchQuery, pageToken)
        }
    }
    fun loadMorePlaylists() {
        val playlistsInfo = repository.getPlaylistsInfo()
        playlistsInfo?.let {
            searchPlaylists(it.searchQuery, it.pageToken)
        }
    }

    // PlaylistItems fetch functions ========================
    fun fetchPlaylistItems(playlistId: String, pageToken: String? = null) {
        launchDataLoad {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
        }
    }
    fun loadMorePlaylistItem() {
        val playlistItemsInfo = repository.getPlaylistItemsInfo()
        playlistItemsInfo?.let {
            fetchPlaylistItems(it.playlistId, it.pageToken)
        }
    }

    // Youtube player handle functions =====================
    fun setCurVideoId(videoId: String) {
        // find index with videoId
        currentPlayInfo.reset()
        currentPlayInfo.videoIndex = playlistItems.value?.find{
            it.id == videoId
        }?.idx?:1
    }
    fun getCurVideoId() : String? {
        if (repository.getPlaylistItemsSize() == 0)
            return null

        return playlistItems.value?.get(currentPlayInfo.videoIndex)?.id
    }
    fun getNextVideoId(): String? {
        // check if play position is at the end of list.
        if ((currentPlayInfo.videoIndex+1 == repository.getPlaylistItemsSize()) &&
            (repository.getPlaylistItemsInfo()?.pageToken != null)) {
            loadMorePlaylistItem()
            return null
        }

        currentPlayInfo.videoSec = 0f

        if (currentPlayInfo.videoIndex < repository.getPlaylistItemsSize())
            currentPlayInfo.videoIndex++
        else
            currentPlayInfo.reset()

        return playlistItems.value?.get(currentPlayInfo.videoIndex)?.id
    }

    // Coroutine helper
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
    var isFullScreen:Boolean)
{
    fun reset() {
        videoIndex = 0
        videoSec = 0f
    }
}

