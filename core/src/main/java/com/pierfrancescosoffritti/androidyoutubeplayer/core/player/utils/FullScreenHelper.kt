package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils

import android.view.View
import android.view.ViewGroup

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener

import java.util.HashSet

internal class FullScreenHelper(private val targetView: View) {

    var isFullScreen: Boolean = false
        private set

    private var isFirstEnter: Boolean = true
    private var widthBackup: Int = 0
    private var heightBackup: Int = 0

    private val fullScreenListeners: MutableSet<YouTubePlayerFullScreenListener> = HashSet()

    fun enterFullScreen() {
        if (isFullScreen) return

        isFullScreen = true

        val viewParams = targetView.layoutParams

        // 뭔가 버그로 여기 두번 들어옴. 첫번째 값을 저장하기 위한 코드
        if ( isFirstEnter) {
            widthBackup = viewParams.width
            heightBackup = viewParams.height
            isFirstEnter = false
        }

        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        targetView.layoutParams = viewParams

        for (fullScreenListener in fullScreenListeners)
            fullScreenListener.onYouTubePlayerEnterFullScreen()
    }

    fun exitFullScreen() {
        if (!isFullScreen) return

        isFullScreen = false
        isFirstEnter = true

        val viewParams = targetView.layoutParams
//        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.height = heightBackup
        viewParams.width = widthBackup
        targetView.layoutParams = viewParams

        for (fullScreenListener in fullScreenListeners)
            fullScreenListener.onYouTubePlayerExitFullScreen()
    }

    fun toggleFullScreen() {
        if (isFullScreen) exitFullScreen()
        else enterFullScreen()
    }

    fun addFullScreenListener(fullScreenListener: YouTubePlayerFullScreenListener): Boolean {
        return fullScreenListeners.add(fullScreenListener)
    }

    fun removeFullScreenListener(fullScreenListener: YouTubePlayerFullScreenListener): Boolean {
        return fullScreenListeners.remove(fullScreenListener)
    }
}
