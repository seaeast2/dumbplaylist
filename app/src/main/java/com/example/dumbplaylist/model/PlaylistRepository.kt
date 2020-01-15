package com.example.dumbplaylist.model


// Repository 에 있어야 할 기능은?
class PlaylistRepository private constructor(
    private val playlistDao: PlaylistDao,
    private val playItemDao: PlayItemDao,
    private val youtubeService: NetworkService
) {
    // playlist, playlist item Dao 접근 인터페이스
    val playlists = playlistDao.getPlaylists()
    val playItems = playItemDao.getPlayItems()

    // Playlist 관련 함수 ================================================
    private fun shouldUpdatePlaylistsCache(): Boolean {
        // TODO : suspending function, so you can e.g. check the status of the database here
        return true
    }
    suspend fun tryUpdateRecentPlaylistsCache() {
        if (shouldUpdatePlaylistsCache())
            fetchRecentPlaylists()
    }
    private suspend fun fetchRecentPlaylists() {
        val playlists = youtubeService.fetchRecentPlaylists()
        playlistDao.insertAll(playlists) // Database 에 캐쉬로 저장함.
    }

    // Play item 관련 함수 ================================================
    private fun shouldUpdatePlayItemsCache(): Boolean {
        // TODO : suspending function, so you can e.g. check the status of the database here
        return true
    }
    suspend fun tryUpdatePlayItemsCache() {
        if (shouldUpdatePlayItemsCache())
            fetchPlayItems()
    }
    private suspend fun fetchPlayItems() {
        val playItems = youtubeService.fetchPlayItems()
        playItemDao.insertAll(playItems)
    }


    // Singleton ================================================
    companion object {
        // For singleton instanciation
        @Volatile private var instance: PlaylistRepository? = null

        fun getInstance(playlistDao: PlaylistDao, playlistItemDao: PlayItemDao, youtubeService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlaylistRepository(playlistDao, playlistItemDao, youtubeService).also {
                    instance = it
                }
            }
    }
}