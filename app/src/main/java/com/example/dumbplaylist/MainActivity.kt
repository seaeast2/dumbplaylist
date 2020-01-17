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


        /* Befor Navigation
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlaylistsFragment.newInstance())
                .commitNow()
        }*/
    }

}
