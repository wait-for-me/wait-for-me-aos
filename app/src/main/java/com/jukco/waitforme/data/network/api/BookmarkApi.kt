package com.jukco.waitforme.data.network.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface BookmarkApi {
    @POST("bookmark/{shopId}")
    suspend fun postBookmark(@Path("shopId") id: Int): Response<Boolean>
}