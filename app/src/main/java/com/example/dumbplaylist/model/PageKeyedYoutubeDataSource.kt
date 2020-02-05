package com.example.dumbplaylist.model

import androidx.paging.PageKeyedDataSource

class PageKeyedYoutubeDataSource: PageKeyedDataSource<String, PlaylistItem>() {

    // 최초 로딩 설정. 최초 1번만 호출됨.
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PlaylistItem>
    ) {

    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, PlaylistItem>
    ) {

    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, PlaylistItem>
    ) {

    }


}