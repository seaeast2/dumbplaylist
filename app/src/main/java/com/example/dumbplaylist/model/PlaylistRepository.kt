package com.example.dumbplaylist.model


class PlaylistRepository private constructor(
    private val playlistDao: PlaylistDao,
    private val playlistItemDao: PlaylistItemDao,
    private val youtubeService: NetworkService
) {
    // playlists
    val playlists = playlistDao.getPlaylists()
    var playlistInfo : PlaylistsInfo = PlaylistsInfo("", 0, 0, "")

    // playlist items
    val playlistItems = playlistItemDao.getPlaylistItems()
    var playlistItemInfo : PlaylistsInfo = PlaylistsInfo("", 0, 0, "")

    // Playlist 관련 함수 ================================================
    private fun shouldUpdatePlaylistsCache(): Boolean {
        // TODO : suspending function, so you can e.g. check the status of the database here
        return true
    }
    suspend fun tryUpdatePlaylistsCache(searchQuery: String, pageToken: String) {
        if (shouldUpdatePlaylistsCache())
            fetchPlaylistsSearchResult(searchQuery, pageToken)
    }

    private suspend fun fetchPlaylistsSearchResult(searchQuery: String, pageToken: String) {
        val result = youtubeService.fetchPlaylistsSearchResult(searchQuery, pageToken)
        playlistInfo = result.second
        playlistDao.insertAll(result.first) // Database 에 캐쉬로 저장함.
    }

    suspend fun clearPlaylists() = playlistDao.deleteAll()

    // Play item 관련 함수 ================================================
    private fun shouldUpdatePlayItemsCache(): Boolean {
        // TODO : suspending function, so you can e.g. check the status of the database here
        return true
    }
    suspend fun tryUpdatePlayItemsCache(playlistId: String, pageToken: String?) {
        if (shouldUpdatePlayItemsCache())
            fetchPlaylistItems(playlistId, pageToken)
    }
    private suspend fun fetchPlaylistItems(playlistId: String, pageToken: String?) {
        val result = youtubeService.fetchPlaylistItems(playlistId, pageToken)
        playlistItemInfo = result.second
        playlistItemDao.insertAll(result.first)
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

