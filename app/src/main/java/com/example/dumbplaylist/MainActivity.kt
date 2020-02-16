package com.example.dumbplaylist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.dumbplaylist.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding : MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // After Navigation
        mBinding = setContentView<MainActivityBinding>(this, R.layout.main_activity)
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
