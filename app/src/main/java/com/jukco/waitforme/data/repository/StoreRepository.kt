package com.jukco.waitforme.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jukco.waitforme.data.network.api.StoreApi
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.StoreDetailResponse
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.network.model.paging.StoresPagingSource
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    suspend fun getStoreList(
        title: String? = null,
        sorter: ShopSorter? = null,
        size: Int = 10,
    ): Flow<PagingData<StoreDto>>

    suspend fun getStore(id: Int): StoreDetailResponse
}

class StoreRepositoryImplementation(
    private val storeApi: StoreApi,
) : StoreRepository {
    override suspend fun getStoreList(
        title: String?,
        sorter: ShopSorter?,
        size: Int,
    ): Flow<PagingData<StoreDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = { StoresPagingSource(storeApi, title, sorter) }
        ).flow
    }

    override suspend fun getStore(id: Int): StoreDetailResponse = storeApi.getStore(id)
}