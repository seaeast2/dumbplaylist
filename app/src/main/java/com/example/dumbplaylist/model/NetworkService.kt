package com.example.dumbplaylist.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class PlaylistsInfo(val id: String, val totalResult:Int, val resultsPerPage:Int, val nextPageToken:String)

//val youtTube: YouTube
class NetworkService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val youtubeService = retrofit.create(YoutubeService::class.java)

    suspend fun fetchPlaylistsSearchResult(searchQuery: String, pageToken: String):
            Pair<List<Playlist>, PlaylistsInfo> = withContext(Dispatchers.Default) {

        var result: List<Playlist> = emptyList()
        var searchResult: PlaylistsSearchResult? = null

        try {
            searchResult = youtubeService.fetchSearchResult(searchQuery, pageToken)
        } catch (e : Exception) {
            print(e)
            return@withContext Pair<List<Playlist>, PlaylistsInfo>(listOf(Playlist("error", e.toString(), "", "")),
                PlaylistsInfo("", 0, 0, ""))
        }

        result = List<Playlist>(searchResult!!.pageInfo.resultsPerPage) {
            Playlist(
                searchResult.items[it].id.playlistId,
                searchResult.items[it].snippet.title,
                searchResult.items[it].snippet.description,
                searchResult.items[it].snippet.thumbnails?.default?.url)
        }

        // save playlist meta info to query next page
        Pair(result, PlaylistsInfo(searchQuery, searchResult.pageInfo.totalResults,
            searchResult.pageInfo.resultsPerPage, searchResult.nextPageToken?:""))
    }

    suspend fun fetchPlaylistItems(playlistId: String, pageToken: String?):
            Pair<List<PlaylistItem>, PlaylistsInfo> = withContext(Dispatchers.Default) {
        val requestResult = youtubeService.fetchPlaylistItems(playlistId, pageToken)
        val result = List<PlaylistItem>(requestResult.pageInfo.resultsPerPage) {
            PlaylistItem(
                requestResult.items[it].snippet.resourceId.videoId,
                requestResult.items[it].snippet.title,
                requestResult.items[it].snippet.description,
                requestResult.items[it].snippet.publishedAt,
                requestResult.items[it].snippet.thumbnails?.default?.url)
        }

        // save playlist meta info to query next page
        Pair(result, PlaylistsInfo(playlistId, requestResult.pageInfo.totalResults,
            requestResult.pageInfo.resultsPerPage, requestResult.nextPageToken?:""))
    }
}

// Key sample : AIzaSyAdDix7i7a3an-gyXiquTV_14cIsr8-DZg
// Key seaeast22 : AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A
// Key seaeast2 : AIzaSyBOm13DNySWTiLFdVL1oHOlK2RKJbOVRDo
interface YoutubeService {
    // Search
    @GET("youtube/v3/search?part=snippet&type=playlist&key=AIzaSyAdDix7i7a3an-gyXiquTV_14cIsr8-DZg&maxResults=50")
    suspend fun fetchSearchResult(@Query("q")searchTerm: String,
                                  @Query("pageToken")pageToken: String): PlaylistsSearchResult

    // fetch playlist items
    @GET("youtube/v3/playlistItems?part=snippet&key=AIzaSyAdDix7i7a3an-gyXiquTV_14cIsr8-DZg&maxResults=50")
    suspend fun fetchPlaylistItems(@Query("playlistId")playlistId: String,
                                   @Query("pageToken")pageToken: String?): PlaylistItemsList
}
