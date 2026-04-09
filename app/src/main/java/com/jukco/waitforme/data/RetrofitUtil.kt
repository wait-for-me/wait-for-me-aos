package com.jukco.waitforme.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jukco.waitforme.BuildConfig
import com.jukco.waitforme.data.network.api.BookmarkApi
import com.jukco.waitforme.data.network.api.SignApi
import com.jukco.waitforme.data.network.api.StoreApi
import com.jukco.waitforme.data.network.api.UserApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitUtil {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json{
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BuildConfig.SERVER_URL)
        .build()

    val signApi: SignApi by lazy {
        retrofit.create(SignApi::class.java)
    }
    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
    val storeApi: StoreApi by lazy {
        retrofit.create(StoreApi::class.java)
    }
    val bookmarkApi: BookmarkApi by lazy {
        retrofit.create(BookmarkApi::class.java)
    }
}