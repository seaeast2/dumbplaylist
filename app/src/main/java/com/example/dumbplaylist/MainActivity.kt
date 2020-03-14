package com.example.dumbplaylist

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dumbplaylist.databinding.MainActivityBinding
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding : MainActivityBinding
    private var mCurrentNavController: LiveData<NavController>? = null

    // viewModel 은 observe 되기 전에 항상 생성되어 있어야 함.
    // 그래서 class 생성시 초기화 되도록 property delegation 으로 처리
    val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // After Navigation
        mBinding = setContentView<MainActivityBinding>(this, R.layout.main_activity)

        //viewModel.initializer = true
        // 최초 실행
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }


    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val navController = findNavController(R.id.nav_host_fragment)
        mBinding.bottomNav.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return mCurrentNavController?.value?.navigateUp() ?: false
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
