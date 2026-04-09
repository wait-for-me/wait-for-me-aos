package com.jukco.waitforme.data.network.api

import com.jukco.waitforme.data.network.model.PagerList
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.StoreDetailResponse
import com.jukco.waitforme.data.network.model.StoreListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApi {
    @GET("shop")
    suspend fun getStoreList(
        @Query("title") title: String? = null,
        @Query("sorter") sorter: ShopSorter? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        ): Response<PagerList<StoreListResponse>>

    @GET("shop/{id}")
    suspend fun getStore(@Path("id") id: Int): StoreDetailResponse
}