package com.jukco.waitforme.data.repository

import com.jukco.waitforme.data.network.api.BookmarkApi
import retrofit2.Response

interface BookmarkRepository {
    suspend fun postBookmark(shopId: Int): Response<Boolean>
}

class BookmarkRepositoryImplementation(
    private val bookmarkApi: BookmarkApi,
) : BookmarkRepository {
    override suspend fun postBookmark(shopId: Int): Response<Boolean> = bookmarkApi.postBookmark(shopId)
}