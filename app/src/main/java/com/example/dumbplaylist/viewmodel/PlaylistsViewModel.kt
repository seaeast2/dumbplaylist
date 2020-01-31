package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.*
import com.example.dumbplaylist.model.AppDatabase
import com.example.dumbplaylist.model.Playlist
import com.example.dumbplaylist.model.PlaylistItem
import com.example.dumbplaylist.model.PlaylistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: PlaylistRepository) : ViewModel() {
    //private val GOOGLE_YOUTUBE_API_KEY: String = "AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A"

    // playlist 와 playlistItem 의 참조를 연결함
    val playlists = repository.playlists // LiveData<List<Playlist>>
    val playlistItems = repository.playlistItems // LiveData<List<PlaylistItem>>
    val playlistItemsPaged = repository.playlistItemsPaged // LiveData<PagedList<PlaylistItem>>

    // video control values
    private var curVideoPos: Int = 0
    var doesRunFullScreen = false
    var curSecond:Float = 0f


    // Spiner 를 위한 변수
    private var mSpiner = MutableLiveData<Boolean>(false)
    // Snackbar 를 위한 문자열
    private var mSnackbarMsg = MutableLiveData<String?>()

    // 여기서 playlists 관련 함수들 ========================
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

    // PlaylistItems 관련 함수들 ========================
    fun fetchPlaylistItems(playlistId: String, pageToken: String = "") {
        launchDataLoad {
            repository.tryUpdatePlayItemsCache(playlistId, pageToken)
        }
    }
    fun clearPlaylistItems() {
        launchDataLoad {
            repository.clearPlaylistItems()
        }
    }

    fun resetVideo() {
        curVideoPos = 0
        curSecond = 0f
    }

    fun getCurVideo(isPaged: Boolean) : String? = if (isPaged)
        playlistItemsPaged.value?.get(curVideoPos)?.id
    else
        playlistItems.value?.get(curVideoPos)?.id

    fun getNextVideo(isPaged: Boolean): String? {
        curSecond = 0f

        if (isPaged) {
            return if (curVideoPos < playlistItemsPaged.value?.size ?: 0) {
                playlistItemsPaged.value?.get(curVideoPos)?.id.apply {
                    curVideoPos++
                }
            } else {
                resetVideo()
                playlistItemsPaged.value?.get(curVideoPos)?.id.apply {
                    curVideoPos++
                }
            }
        }
        else {
            return if (curVideoPos < playlistItems.value?.size ?: 0) {
                playlistItemsPaged.value?.get(curVideoPos)?.id.apply {
                    curVideoPos++
                }
            } else {
                resetVideo()
                playlistItems.value?.get(curVideoPos)?.id.apply {
                    curVideoPos++
                }
            }
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

