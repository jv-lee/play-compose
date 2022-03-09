package com.lee.playcompose.system.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.extensions.singlePager
import com.lee.playcompose.system.model.api.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 体系内容 viewModel
 */
class SystemContentViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        singlePager { api.getParentTabAsync() }
    }

    var viewStates by mutableStateOf(SystemContentViewState(pagingData = pager))
        private set
}

data class SystemContentViewState(
    val pagingData: Flow<PagingData<ParentTab>>,
    val listState: LazyListState = LazyListState()
)