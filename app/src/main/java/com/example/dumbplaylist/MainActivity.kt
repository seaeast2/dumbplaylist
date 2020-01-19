package com.example.dumbplaylist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.dumbplaylist.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // After Navigation
        setContentView<MainActivityBinding>(this, R.layout.main_activity)

        /* Before Navigation
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) { //Bundle 은 최초 실행시에만 null 임
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlaylistsFragment.newInstance())
                .commitNow()
        }*/
    }

}
