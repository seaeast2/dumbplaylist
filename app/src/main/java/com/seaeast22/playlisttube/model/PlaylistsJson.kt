package com.seaeast22.playlisttube.model

data class PlaylistsSearchResult(
    val kind: String,
    val etag: String,
    val nextPageToken: String?,
    val prevPageToken: String?,
    val regionCode: String?,
    val pageInfo: PageInfo,
    val items: List<PlaylistsItem>)

// YouTube PlaylistItems 목록
data class PlaylistItemsList(val kind: String,
                             val etag: String,
                             val nextPageToken: String?,
                             val prevPageToken: String?,
                             val regionCode: String?,
                             val pageInfo: PageInfo,
                             val items: List<PlaylistItemsItem>)


data class PageInfo(val totalResults: Int,
                    val resultsPerPage: Int)

// for playlist
data class PlaylistsItem(val kind: String,
                         val etag: String,
                         val id: PlaylistId, // 이것만 다름
                         val snippet: SnippetForSearch)
data class PlaylistId(val kind: String,
                      val playlistId: String)

// for video list
data class PlaylistItemsItem(val kind: String?,
                     val etag: String?,
                     val id: String?, // 이것만 다름
                     val snippet: SnippetForPlaylistItems)
// for playlists
data class SnippetForSearch(val publishedAt: String?,
                            val channelId: String?,
                            val title: String?,
                            val description: String?,
                            val thumbnails: Thumbnails?,
                            val channelTitle: String?,
                            val liveBroadcastContent: String?)

data class SnippetForPlaylistItems(val publishedAt: String?,
                                   val channelId: String?,
                                   val title: String?,
                                   val description: String?,
                                   val thumbnails: Thumbnails?,
                                   val channelTitle: String?,
                                   val playlistId: String?,
                                   val position: Int?,
                                   val resourceId: ResourceId)

data class ResourceId(val kind: String,
                      val videoId: String)

data class Thumbnails( val default: Thumbnail?,
                       val medium: Thumbnail?,
                       val high: Thumbnail?)

data class Thumbnail(val url: String?,
                     val width: Int?,
                     val height: Int?)

