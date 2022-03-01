package com.lee.playcompose.home.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lee.playcompose.common.entity.Banner
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.extensions.pager
import com.lee.playcompose.home.model.api.ApiService
import com.lee.playcompose.home.model.entity.HomeCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/2/28
 * @description
 */
class HomeViewModel: ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        pager { api.getContentDataAsync(it) }.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(HomeViewState(pagingData = pager))
        private set

    init {
        dispatch(HomeViewAction.RequestData)
    }

    fun dispatch(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.RequestData -> requestData()
        }
    }

    private fun requestData() {
        val bannerFlow = flow {
            emit(api.getBannerDataAsync().data)
        }
        val categoryFlow = flow {
            emit(HomeCategory.getHomeCategory())
        }

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