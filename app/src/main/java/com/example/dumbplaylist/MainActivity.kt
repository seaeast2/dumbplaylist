package com.example.dumbplaylist

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.viewModels
import com.example.dumbplaylist.databinding.MainActivityBinding
import com.example.dumbplaylist.util.Injector
import com.example.dumbplaylist.viewmodel.PlaylistsViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PlaylistsViewModel by viewModels {
        Injector.providePlaylistViewModelFactory(this)
    }

    private lateinit var mBinding : MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // After Navigation
        mBinding = setContentView<MainActivityBinding>(this, R.layout.main_activity)


        setSupportActionBar(mBinding?.myToolbar)

        supportActionBar?.setDisplayShowCustomEnabled(true) // need it to customize
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_drawer_menu_24dp)


        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 서치뷰의 내용으로 검색을 수행할 때 호출 됨
                //Log.d(TAG, "onQueryTextSubmit: " + query);
                query?.let {
                    viewModel.searchPlaylists(query)
                }
                return true;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 서치뷰의 글자가 변경될 때마다 호출 됨
                Log.d("MainActivity", "onQueryTextChange: " + newText);
                return true;
            }
        })

        // 서치뷰가 열린 상태로
        mBinding.searchView.setIconified(true   );

        // 쿼리 힌트
        mBinding.searchView.setQueryHint("이것은 힌트입니다");
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//
//        menuInflater.inflate(R.menu.playlist_frag_menu, menu)
//        val searchItem = menu.findItem(R.id.action_search)
//        val searchView = searchItem.actionView as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                Log.d(TAG, "onQueryTextSubmit: $query")
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                Log.d(TAG, "onQueryTextChange: $newText")
//                return true
//            }
//        })
//        return true
//    }

    companion object {
        private val TAG = "MainActivity"
    }
}
