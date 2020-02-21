package com.example.dumbplaylist

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

open class PlaylistApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
    }
}