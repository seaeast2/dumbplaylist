package com.seaeast22.dumbplaylist.viewmodel

import androidx.lifecycle.*
import com.seaeast22.dumbplaylist.adapter.SelectedPlaylist
import com.seaeast22.dumbplaylist.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    // LiveData
    val playlists = repository.playlists
    val playlistItems = repository.playlistItems
    val savedlists = repository.savedPlaylists

    var playlistInfo : PlaylistInfo = PlaylistInfo()

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
    fun loadMorePlaylists(searchQuery: String, pageToken: String? = null) {
        launchDataUpdate {
            repository.tryUpdatePlaylistsCache(searchQuery, pageToken)
        }
    }

    // PlaylistItems fetch functions ========================
    fun fetchPlaylistItems(playlistId: String, pageToken: String? = null) {
        launchDataUpdate {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
        }
    }
    fun loadMorePlaylistItem(playlistId: String, pageToken: String? = null) {
        launchDataUpdate {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
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

    data class PlaylistInfo(val count:Int = 0, val lastItem: Playlist? = null)
}



