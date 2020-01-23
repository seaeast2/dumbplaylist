package com.example.dumbplaylist.model


// Repository 에 있어야 할 기능은?
class PlaylistRepository private constructor(
    private val playlistDao: PlaylistDao,
    private val playlistItemDao: PlaylistItemDao,
    private val youtubeService: NetworkService
) {
    // playlist, playlist item Dao 접근 인터페이스
    val playlists = playlistDao.getPlaylists()
    val playItems = playlistItemDao.getPlayItems()

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
        val playlists = youtubeService.fetchPlaylistsSearchResult(searchQuery, pageToken)
        playlistDao.insertAll(playlists) // Database 에 캐쉬로 저장함.
    }

    suspend fun clearPlaylists() = playlistDao.deleteAll()

    // Play item 관련 함수 ================================================
    private fun shouldUpdatePlayItemsCache(): Boolean {
        // TODO : suspending function, so you can e.g. check the status of the database here
        return true
    }
    suspend fun tryUpdatePlayItemsCache(playlistId: String, pageToken: String) {
        if (shouldUpdatePlayItemsCache())
            fetchPlaylistItems(playlistId, pageToken)
    }
    private suspend fun fetchPlaylistItems(playlistId: String, pageToken: String) {
        val playItems = youtubeService.fetchPlaylistItems(playlistId, pageToken)
        playlistItemDao.insertAll(playItems)
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