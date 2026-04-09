package com.jukco.waitforme.data.mock

import com.jukco.waitforme.data.mock.MockDataSource.storeDetailList
import com.jukco.waitforme.data.mock.MockDataSource.storeListResponse
import com.jukco.waitforme.data.network.api.StoreApi
import com.jukco.waitforme.data.network.model.PagerList
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.StoreDetailResponse
import com.jukco.waitforme.data.network.model.StoreListResponse
import retrofit2.Response
import java.net.HttpURLConnection

object MockStoreApi : StoreApi {
    override suspend fun getStoreList(
        title: String?,
        sorter: ShopSorter?,
        page: Int?,
        size: Int?
    ): Response<PagerList<StoreListResponse>> {
        val listResponse = storeListResponse.run {
            if (title != null) {
                filter { it.title.contains(title) }
            }
            if (sorter == null || sorter == ShopSorter.NEWEST) {
                sortedByDescending { it.startDate }
            } else {
                sortedBy { it.startDate }
            }
        }
        val pageSize = size ?: 0

        return if ((page ?: 0) < 4) {
            Response.success(
                HttpURLConnection.HTTP_OK,
                PagerList(
                    content = listResponse,
                    number = page ?: 0,
                    size = pageSize,
                    totalPages = listResponse.size / pageSize + if (listResponse.size % pageSize > 0) 1 else 0,
                    totalElements = listResponse.size,
                ),
            )
        } else {
            Response.success(
                HttpURLConnection.HTTP_OK,
                PagerList(
                    content = emptyList(),
                    number = page ?: 0,
                    size = pageSize,
                    totalPages = listResponse.size / pageSize + if (listResponse.size % pageSize > 0) 1 else 0,
                    totalElements = listResponse.size,
                ),
            )
        }
    }

    override suspend fun getStore(id: Int): StoreDetailResponse {
        return storeDetailList[id]
    }

}