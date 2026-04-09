package com.jukco.waitforme.data.network.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jukco.waitforme.data.network.api.StoreApi
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.network.model.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class StoresPagingSource(
    private val api: StoreApi,
    private val title: String?,
    private val sorter: ShopSorter?,
) : PagingSource<Int, StoreDto>() {
    override fun getRefreshKey(state: PagingState<Int, StoreDto>): Int? {
       return state.anchorPosition?.let { anchorPosition ->
           state.closestPageToPosition(anchorPosition)?.prevKey
       }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreDto> {
        val page = params.key ?: 0
        return try {
            val response = withContext(Dispatchers.IO) {
                api.getStoreList(
                    title = title,
                    sorter = sorter,
                    page = page,
                    size = params.loadSize,
                )
            }

            val list = response.body()?.run {
                content.map { storeListResponse ->  storeListResponse.toDto(totalElements) }
            } ?: emptyList()

            LoadResult.Page(
                data = list,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (list.isEmpty()) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}