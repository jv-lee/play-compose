package com.lee.playcompose.common.paging.saved

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.ktx.getCache
import com.lee.playcompose.base.ktx.putCache
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.paging.extensions.pager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * paging3 分页列表数据三级缓存数据
 * @author jv.lee
 * @date 2022/4/13
 */
class SavedPager<T : Any>(
    viewModel: ViewModel,
    initialKey: Int = 0,
    removedFlow: Flow<MutableList<T>>,
    requestAction: suspend (Int) -> PageData<T>,
    localAction: suspend () -> PageData<T>
) {

    private var pager: Flow<PagingData<T>>
    private var localPager: Flow<PagingData<T>>

    init {
        viewModel.apply {
            pager = pager(initialKey = initialKey) {
                requestAction(it)
            }.combine(removedFlow) { pagingData, removed ->
                pagingData.filter { it !in removed }
            }
            localPager = pager(initialKey = initialKey) { page ->
                if (page == initialKey) {
                    localAction()
                } else {
                    requestAction(page)
                }
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

inline fun <reified T : Any> ViewModel.savedPager(
    initialKey: Int = 0,
    savedKey: String = this::class.java.simpleName,
    removedFlow: Flow<MutableList<T>> = flow { emit(mutableListOf()) },
    crossinline requestAction: suspend (Int) -> PageData<T>
) = SavedPager(
    this,
    initialKey = initialKey,
    removedFlow = removedFlow,
    requestAction = { page ->
        requestAction(page).also {
            if (initialKey == page) CacheManager.getDefault().putCache(savedKey, it)
        }
    },
    localAction = {
        CacheManager.getDefault().getCache(savedKey) ?: PageData(data = emptyList())
    }
)