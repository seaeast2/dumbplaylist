package com.example.dumbplaylist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dumbplaylist.model.Playlist
import com.example.dumbplaylist.model.PlaylistItem

// ViewModel for PlayingFragment
class PlayingViewModel : ViewModel() {
    var videoList : List<VideoItem>? = null

    // video control values
    var currentPlayInfo = PlayInfo()

    //val curPlayInfo = currentPlayInfo

    // Database info
    var playlistItemInfo : PlaylistItemInfo = PlaylistItemInfo()


    // Youtube player handle functions =====================
    fun setCurVideoId(videoId: String) {
        // find index with videoId
        currentPlayInfo.reset()
        currentPlayInfo.videoPosition = videoList?.let {
            it.indexOfFirst {videoItem  ->
                videoItem.videoId == videoId
            }
        }?:0
    }

    fun getCurVideoId() : String? {
        videoList?.let {
            if(it.isNotEmpty()) {
                if (currentPlayInfo.videoPosition < playlistItemInfo.count) {
                    return it[currentPlayInfo.videoPosition].videoId
                }
            }
        }
        return null
    }

    fun getNextVideoId(): String? {
        // check if play position is at the end of list.
        currentPlayInfo.videoSec = 0f

        return if (currentPlayInfo.videoPosition < playlistItemInfo.count) {
            currentPlayInfo.videoPosition++
            videoList?.let {
                it[currentPlayInfo.videoPosition].videoId
            }
        } else {
            currentPlayInfo.videoPosition++
            currentPlayInfo.reset()
            null
        }
    }

    // database info

    data class PlaylistItemInfo(val count:Int = 0, val lastItem: PlaylistItem? = null)

    data class VideoItem(val videoId: String = "",
                     val title: String? = null,
                     val description: String? = null,
                     val thumbnailUrl: String? = null,
                     val selected: Boolean = false)

    data class PlayInfo (
        var videoPosition: Int = 0,
        var videoSec: Float = 0.0f,
        var isFullScreen: Boolean = false) {
        fun reset() {
            videoPosition = 0
            videoSec = 0f
        }
    }
}