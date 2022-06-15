package com.lee.playcompose.system.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.common.entity.NavigationItem
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.saved.SavedSinglePager
import com.lee.playcompose.common.paging.saved.singleSavedPager
import com.lee.playcompose.system.model.api.ApiService

/**
 * 导航内容列表 viewModel
 * @author jv.lee
 * @date 2022/3/8
 */
class NavigationContentViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        singleSavedPager {
            api.getNavigationDataAsync().checkData().filter {
                it.articles.isNotEmpty()
            }
        }
    }

    var viewStates by mutableStateOf(NavigationContentViewState(savedPager = pager))
        private set
}

data class NavigationContentViewState(
    val savedPager: SavedSinglePager<NavigationItem>,
    val listState: LazyListState = LazyListState(),
    val tabState: LazyListState = LazyListState()
)