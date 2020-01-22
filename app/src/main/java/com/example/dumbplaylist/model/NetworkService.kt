package com.example.dumbplaylist.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


//val youtTube: YouTube

class NetworkService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/") // TODO : URL 업데이트 요망
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val youtubeService = retrofit.create(YoutubeService::class.java)

    suspend fun fetchRecentPlaylists(): List<Playlist> = withContext(Dispatchers.Default) {
        val searchResult = youtubeService.fetchPlaylists()
        val result = List<Playlist>(searchResult.pageInfo.resultsPerPage) {
            Playlist(searchResult.items[it].id.playlistId,
                searchResult.items[it].snippet.title,
                searchResult.items[it].snippet.description,
                searchResult.items[it].snippet.thumbnails.default.url)
        }
        result
    }

    suspend fun fetchPlayItems(): List<PlayItem> = withContext(Dispatchers.Default) {
        val searchResult = youtubeService.fetchPlayItems()
        val result = List<PlayItem>(searchResult.pageInfo.resultsPerPage) {
            PlayItem(searchResult.items[it].id.videoId,
                searchResult.items[it].snippet.title,
                searchResult.items[it].snippet.description,
                searchResult.items[it].snippet.publishedAt,
                searchResult.items[it].snippet.thumbnails.default.url)
        }
        result
    }
}

interface YoutubeService {
    // 검색
    @GET("youtube/v3/search?part=snippet&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A")
    suspend fun fetchSearchResult(@Query("q")searchTerm: String,
                                   @Query("type")queryType: String = "playlist")

    // PlaylistItems
    @GET("youtube/v3/playlistItems?part=snippet&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A")
    suspend fun fetchPlaylistItems(@Query("id")playlistId: String,
                                   @Query("type")queryType: String = "playlist")

    @GET("youtube/v3/search?part=snippet&q=Kpop&type=playlist&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A")
    suspend fun fetchPlaylists(): PlaylistsJson

    @GET("youtube/v3/search?part=snippet&order=date&channelId=UCGDA1e6qQSAH0R9hoip9VrA&maxResults=20&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A")
    suspend fun fetchPlayItems(): PlayItemsJson
}