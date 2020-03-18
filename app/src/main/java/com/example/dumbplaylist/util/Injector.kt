package com.example.dumbplaylist.util

import android.content.Context
import com.example.dumbplaylist.model.AppDatabase
import com.example.dumbplaylist.model.NetworkService
import com.example.dumbplaylist.model.PlaylistRepository
import com.example.dumbplaylist.ui.MyPlaylistViewModelFactory
import kotlinx.coroutines.flow.combineTransform

interface ViewModelFactoryProvider {
    fun providePlaylistViewModelFactory(context: Context): MyPlaylistViewModelFactory
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