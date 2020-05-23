package com.example.dumbplaylist

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.dumbplaylist.databinding.MainActivityBinding
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.*

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
        setContentView<MainActivityBinding>(this, R.layout.main_activity)

        // After Navigation
        //mBinding = setContentView(this, R.layout.main_activity)

        // 최초 실행
//        if (savedInstanceState == null) {
//            setupBottomNavigationBar()
//        } // Else, need to wait for onRestoreInstanceState

        //initToolBar()

        //mFullScreenHelper = FullScreenHelper(this, mBinding.bottomNav)
        //mFullScreenHelper = FullScreenHelper(this)


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}


        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
//                .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                .build()
        )
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        // Now that BottomNavigationBar has restored its instance state
//        // and its selectedItemId, we can proceed with setting up the
//        // BottomNavigationBar with Navigation
//        //setupBottomNavigationBar()
//    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//    }

//    private fun initToolBar() {
//        val toolBar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolBar)
//    }

    /**
     * Called on first creation and when restoring state.
     */
//    private fun setupBottomNavigationBar() {
//        val navController = findNavController(R.id.nav_host_fragment)
//        mBinding.bottomNav.setupWithNavController(navController)
//    }

    companion object {
        private val TAG = "MainActivity"
    }
}
