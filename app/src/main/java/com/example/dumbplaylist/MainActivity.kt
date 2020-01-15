package com.example.dumbplaylist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dumbplaylist.ui.MyPlaylistFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MyPlaylistFragment.newInstance())
                .commitNow()
        }
    }

}
