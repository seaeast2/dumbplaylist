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
    fun getPlaylistsInfo() = playlists.value?.last()
    fun getPlaylistItemsInfo() = playlistItems.value?.last()

    // Playlist related functions ================================================
    private suspend fun shouldUpdatePlaylistsCache(searchQuery: String, pageToken: String?): Boolean {
        // 이미 저장된 데이터가 있을 경우
        if (getPlaylistsSize() != 0) {
            try {
                if (playlistDao.hasSameSearchQuery(searchQuery).isNotEmpty()) {
                    if (pageToken == null)
                        return false
                    // 이미 해당 page를 가지고 있는 경우
                    if (playlistDao.hasPageToken(pageToken).isNotEmpty())
                        return false
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
        // 이미 저장된 데이터가 있을 경우
        if (getPlaylistItemsSize() != 0) {
            if (playlistItemDao.hasSamePlaylistId(playlistId).isNotEmpty()) {
                try {
                    if (pageToken == null)
                        return false
                    if (playlistItemDao.hasPageToken(pageToken).isNotEmpty())
                        return false
                } catch (e : Exception) {
                    print(e)
                }
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

