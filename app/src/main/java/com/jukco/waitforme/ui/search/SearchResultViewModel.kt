package com.jukco.waitforme.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.jukco.waitforme.config.ApplicationClass
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.repository.BookmarkRepository
import com.jukco.waitforme.data.repository.StoreRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SearchResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val storeRepository: StoreRepository,
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val _searchedStores = MutableStateFlow<PagingData<StoreDto>?>(null)
    private val _recommendStores = MutableStateFlow<PagingData<StoreDto>?>(null)
    val query: String = checkNotNull(savedStateHandle["query"])
    val searchedStores: Flow<PagingData<StoreDto>> = _searchedStores.filterNotNull()
    val recommendStores: Flow<PagingData<StoreDto>> = _recommendStores.filterNotNull()
    var currentTab by mutableIntStateOf(0)
        private set


    init {
        refresh()
    }

    fun refresh(title: String = query, sorter: ShopSorter = ShopSorter.NEWEST) {
        viewModelScope.launch {
            try {
                _searchedStores.value = async {
                    storeRepository.getStoreList(
                        title = title,
                        sorter = sorter
                    ).cachedIn(viewModelScope).first()
                }.await()
            } catch (e: IOException) {
                // TODO: 실패 안내
            } catch (e: HttpException) {
                // TODO: 실패 안내
            }
        }
    }

    fun recommend() {
        viewModelScope.launch {
            try {
                _recommendStores.value = async {
                    storeRepository.getStoreList(sorter = ShopSorter.NEWEST).cachedIn(viewModelScope).first()
                }.await()

            } catch (e: IOException) {
                // TODO: 실패 안내
            } catch (e: HttpException) {
                // TODO: 실패 안내
            }
        }
    }

    fun checkBookmark(shopId: Int) {
        viewModelScope.launch {
            try {
                val response = bookmarkRepository.postBookmark(shopId)
                _searchedStores.update { pagingData ->
                    pagingData?.map { store ->
                        if (store.id == shopId) {
                            store.copy(isFavorite = response.body()!!)
                        } else {
                            store
                        }
                    }
                }

            } catch (e: IOException) {
                // TODO: 실패 안내
            } catch (e: HttpException) {
                // TODO: 실패 안내
            }
        }
    }

    fun sortStores(tab: Int) {
        currentTab = tab

        when (tab) {
            NEWEST_TAB -> {
                refresh(sorter = ShopSorter.NEWEST)
            }
            DEADLINE_TAB -> {
                refresh(sorter = ShopSorter.DEADLINE)
            }
        }
    }


    companion object {
        const val NEWEST_TAB = 0
        const val DEADLINE_TAB = 1

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = this.createSavedStateHandle()
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ApplicationClass)
                val storeRepository = application.container.storeRepository
                val bookmarkRepository = application.container.bookmarkRepository
                SearchResultViewModel(savedStateHandle, storeRepository, bookmarkRepository)
            }
        }
    }
}