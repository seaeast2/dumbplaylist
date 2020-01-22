package com.example.dumbplaylist.model

data class PlaylistsJson(
    val kind: String,
    val etag: String,
    val nextPageToken: String,
    val prevPageToken: String,
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<PlaylistItem>)


data class PlayItemsJson(val kind: String,
                         val etag: String,
                         val nextPageToken: String,
                         val regionCode: String,
                         val pageInfo: PageInfo,
                         val items: List<VideoItem>)


data class PageInfo(val totalResults: Int,
                    val resultsPerPage: Int)

// for playlist
data class PlaylistItem(val kind: String,
                val etag: String,
                val id: PlaylistId, // 이것만 다름
                val snippet: Snippet)
data class PlaylistId(val kind: String,
              val playlistId: String)
// for video list
data class VideoItem(val kind: String,
                     val etag: String,
                     val id: VideoId, // 이것만 다름
                     val snippet: Snippet)
data class VideoId(val kind: String,
                   val videoId: String)


data class Snippet(val publishedAt: String,
                   val channelId: String,
                   val title: String,
                   val description: String,
                   val thumbnails: Thumbnails,
                   val channelTitle: String,
                   val liveBroadcastContent: String)


data class Thumbnails( val default: Thumbnail,
                       val medium: Thumbnail,
                       val high: Thumbnail)

data class Thumbnail(val url: String,
                     val width: Int,
                     val height: Int)

