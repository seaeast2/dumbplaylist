package com.example.dumbplaylist.model

import androidx.lifecycle.Transformations
import androidx.lifecycle.switchMap


class PlaylistRepository private constructor(
    private val playlistDao: PlaylistDao,
    private val playlistItemDao: PlaylistItemDao,
    private val youtubeService: NetworkService
) {
    // LiveData
    val playlists = playlistDao.getPlaylists()
    val playlistItems = playlistItemDao.getPlaylistItems()

    // get Size
    fun getPlaylistsSize(): Int = playlists.value?.size?:0
    fun getPlaylistItemsSize(): Int = playlistItems.value?.size?:0
    // get Info
    fun getPlaylistsInfo() = if (playlists.value?.isEmpty() != false) null else playlists.value?.last()
    fun getPlaylistItemsInfo() = if (playlistItems.value?.isEmpty() != false) null else playlistItems.value?.last()


    // Playlist related functions ================================================
    private suspend fun shouldUpdatePlaylistsCache(searchQuery: String, pageToken: String?): Boolean {
        if (getPlaylistsSize() != 0) {
            try {
                if (playlistDao.hasSameSearchQuery(searchQuery).isNotEmpty()) {
                    if (pageToken == null)
                        return false
                    if (playlistDao.hasPageToken(pageToken).isNotEmpty()) // in case we already have requested page
                        return false

                    // ----> goto return true
                }
                else {
                    // this is new query and we need to clear cache db.
                    playlistDao.deleteAll()
                }
            } catch (e : Exception) {
                print(e)
            }
        }

        return true
    }
    suspend fun tryUpdatePlaylistsCache(searchQuery: String, pageToken: String?) {
        if (shouldUpdatePlaylistsCache(searchQuery, pageToken))
            fetchPlaylistsSearchResult(searchQuery, pageToken)
    }
    private suspend fun fetchPlaylistsSearchResult(searchQuery: String, pageToken: String?) {
        val result =
            youtubeService.fetchPlaylistsSearchResult(searchQuery, pageToken, getPlaylistsSize())
        result?.let {
            playlistDao.insertAll(it)
        }
    }
    suspend fun clearPlaylists() = playlistDao.deleteAll()

    // Play item related functions ================================================
    private suspend fun shouldUpdatePlayItemsCache(playlistId: String, pageToken: String?): Boolean {
        if (getPlaylistItemsSize() != 0) {
            try {
                if (playlistItemDao.hasSamePlaylistId(playlistId).isNotEmpty()) {
                    if (pageToken == null)
                        return false
                    if (playlistItemDao.hasPageToken(pageToken).isNotEmpty())
                        return false

                    // ----> goto return true
                }
                else {
                    playlistItemDao.deleteAll()
                }
            } catch (e : Exception) {
                print(e)
            }
        }

        return true
    }
    suspend fun tryUpdatePlayItemsCache(playlistId: String, pageToken: String?) {
        if (shouldUpdatePlayItemsCache(playlistId, pageToken))
            fetchPlaylistItems(playlistId, pageToken)
    }
    private suspend fun fetchPlaylistItems(playlistId: String, pageToken: String?) {
        val result =
            youtubeService.fetchPlaylistItems(playlistId, pageToken, getPlaylistItemsSize())
        result?.let {
            playlistItemDao.insertAll(it)
        }
    }

    suspend fun clearPlaylistItems() = playlistItemDao.deleteAll()

    // Singleton ================================================
    companion object {
        // For singleton instanciation
        @Volatile private var instance: PlaylistRepository? = null

        fun getInstance(playlistDao: PlaylistDao,
                        playlistItemDao: PlaylistItemDao,
                        youtubeService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlaylistRepository(playlistDao,
                    playlistItemDao, youtubeService).also {
                    instance = it
                }
            }
    }
}

