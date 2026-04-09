package com.jukco.waitforme.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class StoreListResponse(
    @SerialName(value = "id") val id: Int,
    @SerialName(value = "title") val title: String,
    @SerialName(value = "host") val host: String,
    @SerialName(value = "imagePath") val imagePath: String,
    @SerialName(value = "startedAt") val startDate: String,
    @SerialName(value = "isFavorite") val isFavorite: Boolean,
)
