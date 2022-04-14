package com.lee.playcompose.home.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.cacheFlow
import com.lee.playcompose.common.entity.Banner
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.extensions.localPaging
import com.lee.playcompose.home.constants.Constants.CACHE_KEY_HOME_CONTENT
import com.lee.playcompose.home.model.api.ApiService
import com.lee.playcompose.home.model.entity.HomeCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/2/28
 * @description HomePage ViewModel
 */
class HomeViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val pager by lazy {
        localPaging { page ->
            if (page == 0) dispatch(HomeViewAction.RequestData)
            api.getContentDataAsync(page).checkData().data
        }
    }

    var viewStates by mutableStateOf(HomeViewState(pagingData = pager))
        private set

    fun dispatch(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.RequestData -> requestData()
        }
    }

    private fun requestData() {
        val bannerFlow =
            cacheManager.cacheFlow(CACHE_KEY_HOME_CONTENT) { api.getBannerDataAsync().data }
        val categoryFlow = flow { emit(HomeCategory.getHomeCategory()) }

        viewModelScope.launch {
            bannerFlow.zip(categoryFlow) { banners, category ->
                viewStates =
                    viewStates.copy(banners = banners, category = category, isRefreshing = false)
            }.onStart {
                viewStates = viewStates.copy(isRefreshing = true)
            }.catch {
                viewStates = viewStates.copy(isRefreshing = false)
            }.collect()
        }
    }

}

data class HomeViewState(
    val pagingData: Flow<PagingData<Content>>,
    val isRefreshing: Boolean = false,
    val banners: List<Banner> = emptyList(),
    val category: List<HomeCategory> = emptyList(),
    val listState: LazyListState = LazyListState()
)

sealed class HomeViewAction {
    object RequestData : HomeViewAction()
}