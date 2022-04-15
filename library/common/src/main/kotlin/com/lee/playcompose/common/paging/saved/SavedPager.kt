package com.lee.playcompose.common.paging.saved

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.getCache
import com.lee.playcompose.base.extensions.putCache
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.paging.extensions.pager
import com.lee.playcompose.common.paging.extensions.singlePager
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/4/13
 * @description paging3 分页加载本地缓存首页数据
 */
class SavedPager<T : Any>(
    viewModel: ViewModel,
    initialKey: Int = 0,
    requestAction: suspend (Int) -> PageData<T>,
    localAction: suspend () -> List<T>
) {

    private var pager: Flow<PagingData<T>>
    private var localPager: Flow<PagingData<T>>

    init {
        viewModel.apply {
            pager = pager(initialKey = initialKey) {
                requestAction(it)
            }
            localPager = singlePager {
                localAction()
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

    fun flowScope(scope: (Flow<PagingData<T>>) -> Unit): SavedPager<T> {
        scope(pager)
        scope(localPager)
        return this
    }
}

inline fun <reified T : Any> ViewModel.savedPager(
    initialKey: Int = 0,
    remoteKey: String = this::class.java.simpleName,
    crossinline requestAction: suspend (Int) -> PageData<T>
) = SavedPager(this,
    requestAction = { page ->
        requestAction(page).also {
            if (initialKey == page) CacheManager.getDefault().putCache(remoteKey, it.data)
        }
    }, localAction = {
        CacheManager.getDefault().getCache(remoteKey) ?: emptyList()
    })