package com.jukco.waitforme.data.mock

import com.jukco.waitforme.data.network.model.GenderType
import com.jukco.waitforme.data.network.model.NoticeDetailResponse
import com.jukco.waitforme.data.network.model.NoticeResponse
import com.jukco.waitforme.data.network.model.Provider
import com.jukco.waitforme.data.network.model.ImageInfo
import com.jukco.waitforme.data.network.model.SignInResponse
import com.jukco.waitforme.data.network.model.SnsInfo
import com.jukco.waitforme.data.network.model.StoreDetailResponse
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.network.model.StoreListResponse
import com.jukco.waitforme.data.network.model.Token
import com.jukco.waitforme.data.network.model.UserInfoRes
import java.time.LocalDateTime

object MockDataSource {
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
    val signInRes = SignInResponse(
        phoneNumber = "01012345678",
        name = "테스트입니다.",
        isOwner = false,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )

    val noticeList = listOf(
        NoticeResponse(0, "테스트입니다1", LocalDateTime.now()),
        NoticeResponse(1, "테스트입니다2", LocalDateTime.now()),
        NoticeResponse(2, "테스트입니다3", LocalDateTime.now()),
        NoticeResponse(3, "테스트입니다4", LocalDateTime.now()),
        NoticeResponse(4, "테스트입니다5", LocalDateTime.now()),
        NoticeResponse(5, "테스트입니다6", LocalDateTime.now()),
        NoticeResponse(6, "테스트입니다7", LocalDateTime.now()),
    )

    val noticeDetailList = listOf(
        NoticeDetailResponse(
            0,
            "테스트입니다1",
            "1직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
        NoticeDetailResponse(
            1,
            "테스트입니다2",
            "2직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
        NoticeDetailResponse(
            2,
            "테스트입니다3",
            "3직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
        NoticeDetailResponse(
            3,
            "테스트입니다4",
            "4직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
        NoticeDetailResponse(
            4,
            "테스트입니다5",
            "5직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
        NoticeDetailResponse(
            5,
            "테스트입니다6",
            "6직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
        NoticeDetailResponse(
            6,
            "테스트입니다7",
            "7직원정보 업데이트를 위해 NAHAGO 모바일앱을 통해 정보를 등록하여 주시기 바랍니다.\n\n이외 필수 기재사항은 첨부파일을 참고하여 주시기 바랍니다.",
            LocalDateTime.now(),
        ),
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

    val storeDetailList = listOf(
        StoreDetailResponse(
            title = "핑크 홀리데이",
            host = "야놀자",
            description = "핑크 홀리데이! ".repeat(100),
            images = listOf(),
            startDate = "2023-12-24",
            endDate = "2024-01-31",
            openTime = "11:30",
            closeTime = "20:00",
            address = "주소입니다~~",
            snsList = listOf(SnsInfo("INSTAGRAM", "@Sep20_N")),
            isFavorite = false,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "코카콜라",
            host = "코카콜라",
            description = "코카콜라! ".repeat(100),
            images = listOf(),
            startDate = "2023-12-25",
            endDate = "2024-01-14",
            openTime = "10:30",
            closeTime = "23:00",
            address = "주소입니다~~".repeat(2),
            snsList = listOf(),
            isFavorite = true,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "홀리데이",
            host = "여기어때",
            description = "여기어때 ".repeat(100),
            images = listOf(
                ImageInfo("MAIN", "https://cdn.pixabay.com/photo/2016/11/23/15/14/jars-1853439_1280.jpg"),
                ImageInfo("DETAIL", "https://cdn.pixabay.com/photo/2015/11/13/07/47/italy-1041660_1280.jpg"),
                ImageInfo("DETAIL", ""),
            ),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(3),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = true,
            isReserved = true,
        ),
        StoreDetailResponse(
            title = "사이다",
            host = "칠성",
            description = "칠성사이다 ".repeat(100),
            images = listOf(),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(3),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = false,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "어쩌구",
            host = "저쩌구",
            description = "어쩌구저쩌구 ".repeat(100),
            images = listOf(
                ImageInfo("MAIN", "https://cdn.pixabay.com/photo/2016/11/22/21/57/apparel-1850804_1280.jpg"),
                ImageInfo("DETAIL", "https://cdn.pixabay.com/photo/2017/08/07/19/46/shop-2607121_1280.jpg"),
                ImageInfo("DETAIL", ""),
                ImageInfo("DETAIL", ""),
            ),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(5),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = false,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "야호",
            host = "무",
            description = "야호~~~~~~~~~~~~~! ".repeat(100),
            images = listOf(
                ImageInfo("MAIN", "https://cdn.pixabay.com/photo/2017/08/07/19/46/shop-2607121_1280.jpg"),
                ImageInfo("DETAIL", ""),
                ImageInfo("DETAIL", ""),
            ),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(10),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = false,
            isReserved = true,
        ),
        StoreDetailResponse(
            title = "스마일",
            host = "주말이다",
            description = "주말아 와라! ".repeat(100),
            images = listOf(ImageInfo("MAIN", "")),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:30",
            address = "주소입니다~~".repeat(3),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = false,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "핑크",
            host = "Pink",
            description = "핑크 pink ".repeat(100),
            images = listOf(),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(3),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = false,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "떠나요",
            host = "둘이서",
            description = "제주도로 가자! ".repeat(100),
            images = listOf(),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(3),
            snsList = listOf(SnsInfo("FACEBOOK", "www.example.com"), SnsInfo("INSTAGRAM", "@sep20_N")),
            isFavorite = false,
            isReserved = false,
        ),
        StoreDetailResponse(
            title = "I만 다섯",
            host = "mbti",
            description = "IIIII ".repeat(100),
            images = listOf(),
            startDate = "2023-11-24",
            endDate = "2024-02-15",
            openTime = "08:30",
            closeTime = "20:00",
            address = "주소입니다~~".repeat(5),
            snsList = listOf(SnsInfo("INSTAGRAM", "@IonlyFive"), SnsInfo("FACEBOOK", "www.example.com")),
            isFavorite = false,
            isReserved = false,
        ),
    )

    val storeListResponse = listOf(
        StoreListResponse(0, "핑크 홀리데이", "야놀자", "", "2024-07-09", false),
        StoreListResponse(1, "코카콜라", "코카콜라", "", "2024-08-01", true),
        StoreListResponse(2, "홀리데이", "여기어때", "https://cdn.pixabay.com/photo/2016/11/23/15/14/jars-1853439_1280.jpg", "2024-06-09", false),
        StoreListResponse(3, "사이다", "칠성", "", "2024-07-09", true),
        StoreListResponse(4, "어쩌구", "저쩌구", "https://cdn.pixabay.com/photo/2016/11/22/21/57/apparel-1850804_1280.jpg", "2024-06-09", false),
        StoreListResponse(5, "야호", "무", "https://cdn.pixabay.com/photo/2017/08/07/19/46/shop-2607121_1280.jpg", "2024-06-09", false),
        StoreListResponse(6, "스마일", "주말이다", "", "2024-06-21", false),
        StoreListResponse(7, "핑크", "Pink", "", "2024-06-09", false),
        StoreListResponse(8, "떠나요", "둘이서", "", "2024-06-09", false),
        StoreListResponse(9, "I만 다섯", "mbti", "", "2024-06-09", false),
    )

    val storeDtoList = listOf(
        StoreDto(0, "핑크 홀리데이", "야놀자", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(1, "코카콜라", "코카콜라", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",true, 10),
        StoreDto(2, "홀리데이", "여기어때", "https://cdn.pixabay.com/photo/2016/11/23/15/14/jars-1853439_1280.jpg",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(3, "사이다", "칠성", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",true, 10),
        StoreDto(4, "어쩌구", "저쩌구", "https://cdn.pixabay.com/photo/2016/11/22/21/57/apparel-1850804_1280.jpg",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(5, "야호", "무", "https://cdn.pixabay.com/photo/2017/08/07/19/46/shop-2607121_1280.jpg",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(6, "스마일", "주말이다", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(7, "핑크", "Pink", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(8, "떠나요", "둘이서", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
        StoreDto(9, "I만 다섯", "mbti", "",0, "2024.06.09 ~ 2024.07.01", "0000-0000-000",false, 10),
    )
}