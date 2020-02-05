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
        .baseUrl("https://www.googleapis.com/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val youtubeService = retrofit.create(YoutubeService::class.java)

    suspend fun fetchPlaylistsSearchResult(searchQuery: String, pageToken: String): List<Playlist> = withContext(Dispatchers.Default) {
        val searchResult = youtubeService.fetchSearchResult(searchQuery, pageToken)
        //val searchResult = youtubeService.fetchTest()

        val result =
            List<Playlist>(searchResult.pageInfo.resultsPerPage) {
                Playlist(
                    searchResult.items[it].id.playlistId,
                    searchResult.items[it].snippet.title,
                    searchResult.items[it].snippet.description,
                    searchResult.items[it].snippet.thumbnails?.default?.url,
                    searchResult.nextPageToken
                )
            }

        result
    }

    suspend fun fetchPlaylistItems(playlistId: String, pageToken: String): List<PlaylistItem> = withContext(Dispatchers.Default) {
        val requestResult = youtubeService.fetchPlaylistItems(playlistId, pageToken)
        val result = List<PlaylistItem>(requestResult.pageInfo.resultsPerPage) {
            PlaylistItem(
                requestResult.items[it].snippet.resourceId.videoId,
                requestResult.items[it].snippet.title,
                requestResult.items[it].snippet.description,
                requestResult.items[it].snippet.publishedAt,
                requestResult.items[it].snippet.thumbnails?.default?.url,
                requestResult.nextPageToken)
        }
        result
    }
}

interface YoutubeService {
    // Search
    @GET("youtube/v3/search?part=snippet&type=playlist&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A")
    suspend fun fetchSearchResult(@Query("q")searchTerm: String,
                                  @Query("pageToken")pageToken: String): PlaylistsSearchResult

    // fetch playlist items
    @GET("youtube/v3/playlistItems?part=snippet&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A")
    suspend fun fetchPlaylistItems(@Query("playlistId")playlistId: String,
                                   @Query("pageToken")pageToken: String): PlaylistItemsList
}
