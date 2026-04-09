package com.jukco.waitforme.ui.store_list

import androidx.paging.PagingData
import com.jukco.waitforme.data.network.model.StoreDto
import kotlinx.coroutines.flow.Flow

sealed interface StoreListUiState {
    data class Success(
        val ongoingStores: Flow<PagingData<StoreDto>>,
        val upcomingStores: Flow<PagingData<StoreDto>>
    ) : StoreListUiState
    object Error : StoreListUiState
    object Loading : StoreListUiState
}