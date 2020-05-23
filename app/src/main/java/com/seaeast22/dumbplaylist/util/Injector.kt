package com.seaeast22.dumbplaylist.util

import android.content.Context
import com.seaeast22.dumbplaylist.model.AppDatabase
import com.seaeast22.dumbplaylist.model.NetworkService
import com.seaeast22.dumbplaylist.model.PlaylistRepository
import com.seaeast22.dumbplaylist.ui.MyPlayingViewModelFactory
import com.seaeast22.dumbplaylist.ui.MyPlaylistViewModelFactory

interface ViewModelFactoryProvider {
    fun providePlaylistViewModelFactory(context: Context): MyPlaylistViewModelFactory
    fun providePlayingViewModelFactory(context: Context): MyPlayingViewModelFactory
}

// 실제로 사용되는건 이것임
val Injector: ViewModelFactoryProvider
    get() = currentInjector


private object DefaultViewModelProvider: ViewModelFactoryProvider {
    // Get repository
    private fun getRepository(context: Context) =
        PlaylistRepository.getInstance(
            playlistDao(context),
            playlistItemDao(context),
            savedPlaylistDao(context),
            networkService())

    // Get playlist, playlist item Dao object
    private fun playlistDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).playlistDao()
    private fun playlistItemDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).playlistItemDao()
    private fun savedPlaylistDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).savedPlaylistDao()
    private fun networkService() = NetworkService()


    override fun providePlaylistViewModelFactory(context: Context): MyPlaylistViewModelFactory {
        val repository = getRepository(context)
        return MyPlaylistViewModelFactory(repository)
    }

    override fun providePlayingViewModelFactory(context: Context): MyPlayingViewModelFactory {
        return MyPlayingViewModelFactory()
    }
}


private object Lock

@Volatile private var currentInjector: ViewModelFactoryProvider = DefaultViewModelProvider

// Code for unit test
//@VisibleForTesting
//private fun setInjectorForTesting(injector: ViewModelFactoryProvider?) {
//    synchronized(Lock) {
//        currentInjector = injector ?: DefaultViewModelProvider
//    }
//}
//
//@VisibleForTesting
//private fun resetInjector() =
//    setInjectorForTesting(null)