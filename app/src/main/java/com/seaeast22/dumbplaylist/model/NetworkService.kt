package com.seaeast22.dumbplaylist.model

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

    suspend fun fetchPlaylistsSearchResult(searchQuery: String, pageToken: String?, totalLoadedItems: Int): List<Playlist>?  {

        var result: List<Playlist>? = null
        var searchResult: PlaylistsSearchResult? = null

        try {
            searchResult = youtubeService.fetchSearchResult(searchQuery, pageToken)

            result = List<Playlist>(searchResult.items.size) {
                Playlist(
                    totalLoadedItems + it,
                    searchResult.items[it].id.playlistId,
                    searchQuery,
                    searchResult.nextPageToken,
                    searchResult.items[it].snippet.title,
                    searchResult.items[it].snippet.description,
                    searchResult.items[it].snippet.thumbnails?.default?.url)
            }

            result.forEach {
                print("${it.idx} : ${it.playlistId} \n")
            }
        } catch (e : Exception) {
            print(e)
            return null
        }
        // save playlist meta info to query next page
        return result
    }

    suspend fun fetchPlaylistItems(playlistId: String, pageToken: String?, totalLoadedItems: Int): List<PlaylistItem>? {
        var result: List<PlaylistItem>? = null
        var requestResult : PlaylistItemsList? = null

        try {
            requestResult = youtubeService.fetchPlaylistItems(playlistId, pageToken)

            result = List<PlaylistItem>(requestResult.items.size) {
                PlaylistItem(
                    totalLoadedItems + it,
                    requestResult.items[it].snippet.resourceId.videoId,
                    playlistId,
                    requestResult.nextPageToken,
                    requestResult.items[it].snippet.title,
                    requestResult.items[it].snippet.description,
                    requestResult.items[it].snippet.thumbnails?.default?.url)
            }
        } catch (e: Exception) {
            print(e)
            return null
        }

        // save playlist meta info to query next page
        return result
    }
}

// Key seaeast22 : AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A   <== 이거 사용중
// Key seaeast2 : AIzaSyBOm13DNySWTiLFdVL1oHOlK2RKJbOVRDo
interface YoutubeService {
    // Search
    @GET("youtube/v3/search?part=snippet&type=playlist&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A&maxResults=50")
    suspend fun fetchSearchResult(@Query("q")searchTerm: String,
                                  @Query("pageToken")pageToken: String?): PlaylistsSearchResult

    // fetch playlist items
    @GET("youtube/v3/playlistItems?part=snippet&key=AIzaSyDeiMcA8WswiJJu6IyUYit3Zjg7vmo7U9A&maxResults=50")
    suspend fun fetchPlaylistItems(@Query("playlistId")playlistId: String,
                                   @Query("pageToken")pageToken: String?): PlaylistItemsList
}
