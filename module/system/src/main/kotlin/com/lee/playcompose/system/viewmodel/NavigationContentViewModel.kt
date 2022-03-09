package com.lee.playcompose.system.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.lee.playcompose.common.entity.NavigationItem
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.extensions.singlePager
import com.lee.playcompose.system.model.api.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 导航内容 viewModel
 */
class NavigationContentViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        singlePager { api.getNavigationDataAsync() }
    }

    var viewStates by mutableStateOf(NavigationContentViewState(pagingData = pager))
        private set
}

data class NavigationContentViewState(
    val pagingData: Flow<PagingData<NavigationItem>>,
    val listState: LazyListState = LazyListState(),
    val tabState: LazyListState = LazyListState()
)