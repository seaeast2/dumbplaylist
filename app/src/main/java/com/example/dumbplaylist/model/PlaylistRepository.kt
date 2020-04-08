package com.example.dumbplaylist.model


class PlaylistRepository private constructor(
    private val playlistDao: PlaylistDao,
    private val playlistItemDao: PlaylistItemDao,
    private val savedPlaylistDao: SavedPlaylistDao,
    private val youtubeService: NetworkService
) {
    // LiveData
    val playlists = playlistDao.getAll()
    val playlistItems = playlistItemDao.getAll()
    val savedPlaylist = savedPlaylistDao.getAll()

    // get Size
    fun getPlaylistsSize(): Int = playlists.value?.size?:0
    fun getPlaylistItemsSize(): Int = playlistItems.value?.size?:0
    // get Info
    fun getPlaylistsInfo() = if (playlists.value?.isEmpty() != false) null else playlists.value?.last()
    fun getPlaylistItemsInfo() = if (playlistItems.value?.isEmpty() != false) null else playlistItems.value?.last()


    // Playlists related functions ================================================
    private suspend fun shouldUpdatePlaylistsCache(searchQuery: String, pageToken: String?): Boolean {
        try {
            if (getPlaylistsSize() != 0) {
                val lastItem = getPlaylistsInfo()
                lastItem?.let {
                    if (it.searchQuery == searchQuery) {
                        if (pageToken == null)
                            return@shouldUpdatePlaylistsCache false
                        // ----> goto return true
                    } else {
                        // this is new query and we need to clear cache db.
                        playlistDao.deleteAll()
                    }
                }
            }
        } catch (e : Exception) {
            print(e)
        }
        return true
    }
    suspend fun tryUpdatePlaylistsCache(searchQuery: String, pageToken: String?) {
        if (shouldUpdatePlaylistsCache(searchQuery, pageToken))
            fetchPlaylistsSearchResult(searchQuery, pageToken)
    }
    private suspend fun fetchPlaylistsSearchResult(searchQuery: String, pageToken: String?) {
        val itemCount = playlistDao.getItemCount()
        val result =
            youtubeService.fetchPlaylistsSearchResult(searchQuery, pageToken, itemCount)
        result?.let {
            playlistDao.insertAll(it)
        }
    }
    suspend fun clearPlaylists() = playlistDao.deleteAll()

    // Playlist item related functions ================================================
    private suspend fun shouldUpdatePlayItemsCache(playlistId: String, pageToken: String?): Boolean {
        if (getPlaylistItemsSize() != 0) {
            try {
                val lastItem = getPlaylistItemsInfo()
                lastItem?.let {
                    if (it.playlistId == playlistId) {
                        if (pageToken == null)
                            return@shouldUpdatePlayItemsCache false
                        // ----> goto return true
                    }
                    else
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
            youtubeService.fetchPlaylistItems(playlistId, pageToken, playlistItemDao.getItemCount())
        result?.let {
            playlistItemDao.insertAll(it)
        }
    }

    suspend fun clearPlaylistItems() = playlistItemDao.deleteAll()

    // saved playlist related functions ================================================
    suspend fun addSavedPlaylist(savedPlaylist: SavedPlaylist) {
        if (savedPlaylistDao.findByPlaylistId(savedPlaylist.playlistId) == 0) {
            savedPlaylistDao.insertSavedPlaylist(savedPlaylist)
        }
    }

    // Singleton ================================================
    companion object {
        // For singleton instanciation
        @Volatile private var instance: PlaylistRepository? = null

        fun getInstance(playlistDao: PlaylistDao,
                        playlistItemDao: PlaylistItemDao,
                        savedPlaylistDao: SavedPlaylistDao,
                        youtubeService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlaylistRepository(
                    playlistDao,
                    playlistItemDao,
                    savedPlaylistDao,
                    youtubeService).also {
                    instance = it
                }
            }
    }
}

