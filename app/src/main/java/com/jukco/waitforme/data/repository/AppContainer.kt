package com.jukco.waitforme.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import com.jukco.waitforme.BuildConfig
import com.jukco.waitforme.data.auth.AuthProvider
import com.jukco.waitforme.data.auth.GoogleAuthProvider
import com.jukco.waitforme.data.auth.KakaoAuthProvider
import com.jukco.waitforme.data.auth.NaverAuthProvider
import com.jukco.waitforme.data.mock.MockBookmarkRepository
import com.jukco.waitforme.data.mock.MockNoticeRepository
import com.jukco.waitforme.data.mock.MockSignRepository
import com.jukco.waitforme.data.mock.MockStoreApi
import com.jukco.waitforme.data.mock.MockUserRepository
import com.jukco.waitforme.data.network.api.NoticeApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val tokenManager: TokenManager
    val googleAuthProvider: AuthProvider
    val kakaoAuthProvider: AuthProvider
    val naverAuthProvider: AuthProvider
    val noticeRepository: NoticeRepository
    val signRepository: SignRepository
    val storeRepository: StoreRepository
    val userRepository: UserRepository
    val bookmarkRepository: BookmarkRepository
}

class DefaultContainer(context: Context) : AppContainer {
    private val credentialManager = CredentialManager.create(context)

    override val tokenManager: TokenManager by lazy {
        TokenManagerImplementation(context.signInData)
    }

    override val googleAuthProvider: AuthProvider by lazy {
        GoogleAuthProvider(credentialManager, BuildConfig.GOOGLE_SERVER_CLIENT_ID)
    }
    override val kakaoAuthProvider: AuthProvider by lazy {
        KakaoAuthProvider()
    }
    override val naverAuthProvider: AuthProvider by lazy {
        NaverAuthProvider()
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BuildConfig.SERVER_URL)
        .build()

    private val noticeApi: NoticeApi by lazy {
        retrofit.create(NoticeApi::class.java)
    }

    // TODO : 서버 연결 전까지 임시 Repository
    override val noticeRepository: NoticeRepository by lazy {
//        NoticeRepositoryImplementation(noticeApi)
        MockNoticeRepository
    }
    override val signRepository: SignRepository by lazy {
//        SignRepositoryImplementation(signApi)
        MockSignRepository
    }
    override val storeRepository: StoreRepository by lazy {
//        StoreRepositoryImplementation(storeApi)
        StoreRepositoryImplementation(MockStoreApi)
    }
    override val userRepository: UserRepository by lazy {
//        UserRepositoryImplementation(userApi)
        MockUserRepository
    }
    override val bookmarkRepository: BookmarkRepository by lazy {
//        BookmarkRepositoryImplementation(bookmarkApi)
        MockBookmarkRepository
    }
}
