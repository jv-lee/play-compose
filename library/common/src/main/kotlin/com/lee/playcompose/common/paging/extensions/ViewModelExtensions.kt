package com.lee.playcompose.common.paging.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.extensions.checkData
import kotlinx.coroutines.flow.Flow

/**
 * 公共viewModel 功能扩展函数
 * @author jv.lee
 * @date 2022/2/28
 */
fun <T : Any> ViewModel.pager(
    config: PagingConfig = PagingConfig(20),
    initialKey: Int = 0,
    requestAction: suspend (page: Int) -> PageData<T>
): Flow<PagingData<T>> {
    return createPaging(config, initialKey) { params ->
        val page = params.key ?: 0
        try {
            val response = requestAction(page)
            val data = response.data
            val hasNext = (response.curPage < response.pageCount)

            PagingSource.LoadResult.Page(
                data = data,
                prevKey = if (page == initialKey) null else page - 1,
                nextKey = if (hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }
}

fun <T : Any> ViewModel.singlePager(
    config: PagingConfig = PagingConfig(100), initialKey: Int = 0,
    requestAction: suspend () -> List<T>
): Flow<PagingData<T>> {
    return createPaging(config, initialKey) {
        try {
            PagingSource.LoadResult.Page(data = requestAction(), prevKey = null, nextKey = null)
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }
}

private fun <K : Any, V : Any> ViewModel.createPaging(
    config: PagingConfig, initialKey: K? = null,
    requestData: suspend (PagingSource.LoadParams<K>) -> PagingSource.LoadResult<K, V>
): Flow<PagingData<V>> {
    return Pager(config, initialKey) {
        object : PagingSource<K, V>() {
            override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                return requestData(params)
            }

            override fun getRefreshKey(state: PagingState<K, V>): K? {
                return initialKey
            }
        }
    }.flow.cachedIn(viewModelScope)
}