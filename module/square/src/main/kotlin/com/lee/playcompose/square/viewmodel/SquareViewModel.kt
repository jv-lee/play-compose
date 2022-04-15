package com.lee.playcompose.square.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.extensions.savedPager
import com.lee.playcompose.square.model.api.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/3
 * @description 广场页 viewModel
 */
class SquareViewModel : ViewModel() {
    private val api = createApi<ApiService>()

    private val pager by lazy {
        savedPager { api.getSquareDataSync(it).checkData() }
    }

    var viewStates by mutableStateOf(SquareViewState(pagingData = pager))
        private set
}

data class SquareViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)