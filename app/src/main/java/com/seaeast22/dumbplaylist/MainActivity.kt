package com.seaeast22.dumbplaylist

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.seaeast22.dumbplaylist.ui.PlayingFragment
import com.seaeast22.dumbplaylist.util.Injector
import com.seaeast22.dumbplaylist.viewmodel.PLAYING_FRAGMENT
import com.seaeast22.dumbplaylist.viewmodel.PlaylistsViewModel
import com.seaeast22.dumbplaylist.viewmodel.SAVED_FRAGMENT
import com.seaeast22.dumbplaylist.viewmodel.SEARCH_FRAGMENT

class MainActivity : AppCompatActivity() {
    //lateinit var mBinding : MainActivityBinding

    // viewModel 은 observe 되기 전에 항상 생성되어 있어야 함.
    // 그래서 class 생성시 초기화 되도록 property delegation 으로 처리
    val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(this)
    }

    //lateinit var mFullScreenHelper: FullScreenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.main_activity)
    }

    override fun onBackPressed() {
        when (viewModel.currentFragmentType) {
            SEARCH_FRAGMENT -> {
                super.onBackPressed()
            }
            SAVED_FRAGMENT -> {
                super.onBackPressed()
            }
            PLAYING_FRAGMENT -> {
                // 전체화면에서 Back키 누를 경우 처리
                val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                val curFrag = navHostFrag?.childFragmentManager?.fragments?.get(0) as PlayingFragment?
                curFrag?.let {
                    if (it.mPlayingViewModel.currentPlayInfo.isFullScreen) {
                        it.mYouTubePlayerView.exitFullScreen()
                    }
                    else
                        super.onBackPressed()
                }
            }
            else -> {
                super.onBackPressed()
            }
        }

    }
    companion object {
        private val TAG = "MainActivity"
    }
}
