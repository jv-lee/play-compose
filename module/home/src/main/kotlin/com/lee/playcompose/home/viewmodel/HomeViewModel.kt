package com.lee.playcompose.home.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.ktx.cacheFlow
import com.lee.playcompose.common.entity.Banner
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.home.constants.Constants.CACHE_KEY_HOME_CONTENT
import com.lee.playcompose.home.model.api.ApiService
import com.lee.playcompose.home.model.entity.HomeCategory
import kotlinx.coroutines.launch

/**
 * HomePage ViewModel
 * @author jv.lee
 * @date 2022/2/28
 */
class HomeViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    private val pager by lazy {
        savedPager { page ->
            if (page == 0) requestData()
            api.getContentDataAsync(page).checkData()
        }
    }

    var viewStates by mutableStateOf(HomeViewState(savedPager = pager))
        private set

    fun dispatch(intent: HomeViewIntent) {
        when (intent) {
            is HomeViewIntent.RequestLoopBanner -> requestLoopBanner()
        }
    }

    private fun requestData() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_HOME_CONTENT) {
                api.getBannerDataAsync().data
            }.collect { data ->
                val categoryList = HomeCategory.getHomeCategory()
                viewStates =
                    viewStates.copy(banners = data, category = categoryList)
            }
        }
    }

    private fun requestLoopBanner() {
        viewStates = viewStates.copy(isLoop = true)
    }
}

data class HomeViewState(
    val isLoop: Boolean = false,
    val savedPager: SavedPager<Content>,
    val banners: List<Banner> = emptyList(),
    val category: List<HomeCategory> = emptyList(),
    val listState: LazyListState = LazyListState()
)

sealed class HomeViewIntent {
    object RequestLoopBanner : HomeViewIntent()
}