package com.lee.playcompose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.delay

/**
 * @author jv.lee
 * @date 2022/2/28
 * @description
 */
class HomeViewModel : ViewModel() {
    val projects = Pager(PagingConfig(pageSize = 20)) {
        SimpleDataSource()
    }.flow.cachedIn(viewModelScope)

    class SimpleDataSource : PagingSource<Int, String>() {
        override fun getRefreshKey(state: PagingState<Int, String>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
            return try {
                delay(1000)
                val nextPage = params.key ?: 1
                val data = mutableListOf<String>().apply {
                    for (index in 0..params.loadSize) {
                        add("item - $index")
                    }
                }
                LoadResult.Page(
                    data = data,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (nextPage < 5) nextPage + 1 else null
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }
}