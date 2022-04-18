package com.lee.playcompose.common.paging.saved

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.getCache
import com.lee.playcompose.base.extensions.putCache
import com.lee.playcompose.common.paging.extensions.singlePager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * @author jv.lee
 * @date 2022/4/13
 * @description paging3 单页面列表数据三级缓存数据
 */
class SavedSinglePager<T : Any>(
    viewModel: ViewModel,
    removedFlow: Flow<MutableList<T>>,
    requestAction: suspend () -> List<T>,
    localAction: suspend () -> List<T>
) {

    private var pager: Flow<PagingData<T>>
    private var localPager: Flow<PagingData<T>>

    init {
        viewModel.apply {
            pager = singlePager {
                requestAction()
            }.combine(removedFlow) { pagingData, removed ->
                pagingData.filter { it !in removed }
            }
            localPager = singlePager {
                localAction()
            }.combine(removedFlow) { pagingData, removed ->
                pagingData.filter { it !in removed }
            }
        }
    }

    @Composable
    fun getLazyPagingItems(): LazyPagingItems<T> {
        val pagerContent = pager.collectAsLazyPagingItems()
        val localContent = localPager.collectAsLazyPagingItems()

        if (localContent.itemCount == 0 && pagerContent.itemCount == 0) {
            return pagerContent
        }

        return if (pagerContent.itemCount > 0) pagerContent else localContent
    }

}

inline fun <reified T : Any> ViewModel.singleSavedPager(
    remoteKey: String = this::class.java.simpleName,
    removedFlow: Flow<MutableList<T>> = flow { emit(mutableListOf()) },
    crossinline requestAction: suspend () -> List<T>
) = SavedSinglePager(this,
    removedFlow = removedFlow,
    requestAction = {
        requestAction().also {
            CacheManager.getDefault().putCache(remoteKey, it)
        }
    }, localAction = {
        CacheManager.getDefault().getCache(remoteKey) ?: emptyList()
    })