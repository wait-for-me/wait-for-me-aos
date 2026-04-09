package com.jukco.waitforme.fake

import com.jukco.waitforme.data.network.model.GenderType
import com.jukco.waitforme.data.network.model.Provider
import com.jukco.waitforme.data.network.model.SignInResponse
import com.jukco.waitforme.data.network.model.Token
import com.jukco.waitforme.data.network.model.UserInfoRes

object FakeDataSource {
    val accessToken = Token(
        token = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxIiwic3ViIjoi7YWM7Iqk7Yq47J6F64uI64ukLiIsImV4cCI6MTcwNzQwNTU4MiwiYXV0aG9yaXRpZXMiOiJVU0VSIn0.YF4wLj9m9IfJRVxiEFPTskFz8BlMPH6gaqmotlpI0iKt00KK42_ZFztOnYbNNV9vQjp-Mrn10f8nmv6UkCwKkQ",
        createdAt = "2024-02-08T23:49:42.828",
        expiredAt = "2024-02-09T00:19:42.828",
    )
    val refreshToken = Token(
        token = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxIiwic3ViIjoi7YWM7Iqk7Yq47J6F64uI64ukLiIsImV4cCI6MTcwNzQwNzM4MiwiYXV0aG9yaXRpZXMiOiJVU0VSIn0.BEbmx1OW-6ulVUxvrF0RzvElMK8pWCkoLdnFrF9pKOJD4WdYAaSOS7mNYHJVggbi64GNTw1d1_RFUbClfqwe6g",
        createdAt = "2024-02-08T23:49:42.828",
        expiredAt = "2024-02-09T00:49:42.828",
    )
    val signInResponse = SignInResponse(
        phoneNumber = "01012345678",
        name = "테스트입니다.",
        isOwner = false,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )

    val userInfoRes = UserInfoRes(
        provider = Provider.LOCAL,
        phoneNumber = "01012345678",
        name = "테스트입니다.",
        isOwner = false,
        birthedAt = null,
        genderType = GenderType.OTHER,
        profileImage = null,
    )

    val revisedUserInfoRes = UserInfoRes(
        provider = Provider.LOCAL,
        phoneNumber = "01012345678",
        name = "테스트라구요!",
        isOwner = false,
        birthedAt = "1995-01-18",
        genderType = GenderType.FEMALE,
        profileImage = null,
    )
}
