package com.example.dumbplaylist

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dumbplaylist.databinding.MainActivityBinding
import com.example.dumbplaylist.util.FullScreenHelper
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel

class MainActivity : AppCompatActivity() {
    lateinit var mBinding : MainActivityBinding

    // viewModel 은 observe 되기 전에 항상 생성되어 있어야 함.
    // 그래서 class 생성시 초기화 되도록 property delegation 으로 처리
    val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(this)
    }

    lateinit var mFullScreenHelper: FullScreenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        // After Navigation
        mBinding = setContentView(this, R.layout.main_activity)

        // 최초 실행
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        initToolBar()
        mFullScreenHelper = FullScreenHelper(this, mBinding.bottomNav)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    private fun initToolBar() {
        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val navController = findNavController(R.id.nav_host_fragment)
        mBinding.bottomNav.setupWithNavController(navController)

    }

    companion object {
        private val TAG = "MainActivity"
    }
}
