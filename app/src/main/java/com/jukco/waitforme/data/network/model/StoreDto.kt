package com.jukco.waitforme.data.network.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class StoreDto(
    val id: Int,
    val title: String,
    val host: String,
    val imagePath: String,
    val dDay: Long,
    val operatingPeriod: String,
    val registrationNumber: String,
    val isFavorite: Boolean,
    val totalStores: Int,
)

fun StoreListResponse.toDto(totalStores: Int) =
    StoreDto(
        id = id,
        title = title,
        host = host,
        imagePath = imagePath,
        dDay = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(startDate)),
        operatingPeriod = "",
        registrationNumber = "",
        isFavorite = isFavorite,
        totalStores = totalStores,
    )

