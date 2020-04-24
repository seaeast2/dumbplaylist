package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.*
import com.example.dumbplaylist.adapter.SelectedPlaylist
import com.example.dumbplaylist.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    // LiveData
    val playlists = repository.playlists
    val playlistItems = repository.playlistItems
    val savedlists = repository.savedPlaylists

    // video control values
    private var currentPlayInfo = PlayInfo(0, 0f, false)
    val curPlayInfo = currentPlayInfo
    
    // Database info
    var playlistInfo : PlaylistInfo = PlaylistInfo()
    var playlistItemInfo : PlaylistItemInfo = PlaylistItemInfo()

    // laoding Spiner
    private var mSpiner = MutableLiveData<Boolean>(false)
    // error massge popup Snackbar
    private var mSnackbarMsg = MutableLiveData<String?>()

    var selectedPlaylist: SelectedPlaylist? = null

    // playlists fetch functions ========================
    fun searchPlaylists(searchQuery: String, pageToken: String? = null) {
        launchDataUpdate {
            repository.tryUpdatePlaylistsCache(searchQuery, pageToken)
        }
    }
    fun loadMorePlaylists() {
        launchDataUpdate {
            playlistInfo.lastItem?.let {
                repository.tryUpdatePlaylistsCache(it.searchQuery, it.pageToken)
            }
        }
    }

    // PlaylistItems fetch functions ========================
    fun fetchPlaylistItems(playlistId: String, pageToken: String? = null) {
        launchDataUpdate {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
        }
    }
    fun loadMorePlaylistItem() {
        launchDataUpdate {
            playlistItemInfo.lastItem?.let {
                repository.tryUpdatePlayItemsCache(it.playlistId, it.pageToken)
            }
        }
    }

    // Saved Playlist functions ============================
    fun addSavedPlaylist(selectedPlaylist: SelectedPlaylist) {
        launchDataUpdate {
            repository.addSavedPlaylist(SavedPlaylist(
                selectedPlaylist.playlistId,
                selectedPlaylist.title,
                selectedPlaylist.description,
                selectedPlaylist.thumbnailUrl))
        }
    }

    fun removeSavedPlaylist(position: Int) {
        val playlist = savedlists.value?.get(position)
        launchDataUpdate {
            playlist?.let {
                repository.removeSavedPlaylist(it)
            }
        }
    }

    // Youtube player handle functions =====================
    fun setCurVideoId(videoId: String) {
        // find index with videoId
        currentPlayInfo.reset()
        currentPlayInfo.videoPosition = playlistItems.value?.find{
            it.id == videoId
        }?.idx?:1
    }

    fun getCurVideoId() : String? {
        if (playlistItemInfo.count > 0)
            return playlistItems.value?.get(currentPlayInfo.videoPosition)?.id

        return null
    }

    fun getNextVideoId(): String? {
        // check if play position is at the end of list.
        if ((currentPlayInfo.videoPosition+1 == playlistItemInfo.count) &&
            (playlistItemInfo.lastItem?.pageToken != null)) {
            loadMorePlaylistItem()
            return null
        }

        currentPlayInfo.videoSec = 0f

        if (currentPlayInfo.videoPosition < playlistItemInfo.count)
            currentPlayInfo.videoPosition++
        else
            currentPlayInfo.reset()

        return playlistItems.value?.get(currentPlayInfo.videoPosition)?.id
    }

    // Coroutine helper
    private fun launchDataUpdate(block: suspend () -> Unit): Job {
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
    
    data class PlaylistInfo(val count:Int = 0, val lastItem:Playlist? = null)
    data class PlaylistItemInfo(val count:Int = 0, val lastItem:PlaylistItem? = null)

    data class PlayInfo (
        var videoPosition: Int,
        var videoSec: Float,
        var isFullScreen: Boolean) {
        fun reset() {
            videoPosition = 0
            videoSec = 0f
        }
    }
}



