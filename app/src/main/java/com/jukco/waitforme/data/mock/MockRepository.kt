package com.jukco.waitforme.data.mock

import androidx.paging.PagingData
import com.jukco.waitforme.data.mock.MockDataSource.storeDtoList
import com.jukco.waitforme.data.mock.MockDataSource.userInfoRes
import com.jukco.waitforme.data.network.model.LocalSignInRequest
import com.jukco.waitforme.data.network.model.LocalSignUpRequest
import com.jukco.waitforme.data.network.model.NoticeDetailResponse
import com.jukco.waitforme.data.network.model.NoticeResponse
import com.jukco.waitforme.data.network.model.PhoneNumCheckRequest
import com.jukco.waitforme.data.network.model.Provider
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.SignInResponse
import com.jukco.waitforme.data.network.model.SocialSignInRequest
import com.jukco.waitforme.data.network.model.SocialSignUpRequest
import com.jukco.waitforme.data.network.model.StoreDetailResponse
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.network.model.UserInfoRequest
import com.jukco.waitforme.data.network.model.UserInfoRes
import com.jukco.waitforme.data.repository.BookmarkRepository
import com.jukco.waitforme.data.repository.NoticeRepository
import com.jukco.waitforme.data.repository.SignRepository
import com.jukco.waitforme.data.repository.StoreRepository
import com.jukco.waitforme.data.repository.TokenManager
import com.jukco.waitforme.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import java.net.HttpURLConnection
import java.time.LocalDateTime

object MockTokenManager : TokenManager {
    override val providerFlow: Flow<String> = MutableStateFlow("")
    override val userNameFlow: Flow<String> = MutableStateFlow("")
    override val userPhoneNumberFlow: Flow<String> = MutableStateFlow("")
    override val isOwnerFlow: Flow<Boolean> = MutableStateFlow(false)
    override val accessTokenFlow: Flow<String> = MutableStateFlow("")
    override val accessTokenCreatedTimeFlow: Flow<LocalDateTime?> = MutableStateFlow(null)
    override val accessTokenExpiredTimeFlow: Flow<LocalDateTime?> = MutableStateFlow(null)
    override val refreshTokenFlow: Flow<String> = MutableStateFlow("")
    override val refreshTokenCreatedTimeFlow: Flow<LocalDateTime?> = MutableStateFlow(null)
    override val refreshTokenExpiredTimeFlow: Flow<LocalDateTime?> = MutableStateFlow(null)

    override suspend fun saveToken(provider: Provider, signInResponse: SignInResponse) = Unit
    override suspend fun clearToken(provider: Provider) = Unit
    override suspend fun removeAll() = Unit

}

object MockSignRepository : SignRepository {
    override suspend fun localSignIn(signInReq: LocalSignInRequest): Response<SignInResponse> =
        Response.success(HttpURLConnection.HTTP_OK, MockDataSource.signInRes)

    override suspend fun socialSignIn(signInReq: SocialSignInRequest): Response<SignInResponse> =
        Response.success(HttpURLConnection.HTTP_NO_CONTENT, MockDataSource.signInRes)

    override suspend fun localSignUp(signUpReq: LocalSignUpRequest): Response<SignInResponse> =
        Response.success(HttpURLConnection.HTTP_OK, MockDataSource.signInRes)

    override suspend fun socialSignUp(signUpReq: SocialSignUpRequest): Response<SignInResponse> =
        Response.success(HttpURLConnection.HTTP_OK, MockDataSource.signInRes)

    override suspend fun checkUniqueName(name: String): Response<Boolean> =
        Response.success(HttpURLConnection.HTTP_OK, false)

    override suspend fun requestAuthnNum(phoneNum: String): Response<Boolean> =
        Response.success(HttpURLConnection.HTTP_OK, true)

    override suspend fun checkPhoneNumberValidity(request: PhoneNumCheckRequest): Response<Boolean> =
        Response.success(HttpURLConnection.HTTP_OK, true)
}

object MockUserRepository : UserRepository {
    override suspend fun getUserInfo(): Response<UserInfoRes> =
        Response.success(HttpURLConnection.HTTP_OK, userInfoRes)

    override suspend fun editUserInfo(userInfoReq: UserInfoRequest): Response<UserInfoRes> =
        Response.success(
            HttpURLConnection.HTTP_OK,
            userInfoRes.copy(
                name = userInfoReq.name,
                birthedAt = userInfoReq.birthedAt,
                genderType = userInfoReq.genderType,
                profileImage = userInfoReq.profileImage
            )
        )

    override suspend fun withdraw(reason: String): Response<Boolean> =
        Response.success(HttpURLConnection.HTTP_OK, true)
}

object MockBookmarkRepository : BookmarkRepository {
    override suspend fun postBookmark(shopId: Int): Response<Boolean> {
        val index = storeDtoList.indexOfFirst { storeDto -> storeDto.id == shopId }
        return Response.success(HttpURLConnection.HTTP_OK, !storeDtoList[index].isFavorite)
    }
object MockNoticeRepository : NoticeRepository {
    override suspend fun getNoticeList(): Response<List<NoticeResponse>> =
        Response.success(HttpURLConnection.HTTP_OK, MockDataSource.noticeList)

    override suspend fun getNotice(noticeId: Int): Response<NoticeDetailResponse> =
        Response.success(HttpURLConnection.HTTP_OK, MockDataSource.noticeDetailList[noticeId])
}