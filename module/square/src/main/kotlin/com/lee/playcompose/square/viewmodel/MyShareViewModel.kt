package com.lee.playcompose.square.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.extensions.pager
import com.lee.playcompose.square.model.api.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/29
 * @description
 */
class MyShareViewModel : ViewModel() {
    private val api = createApi<ApiService>()

    private val pager by lazy {
        pager { api.getMyShareDataSync(it).checkData().shareArticles }.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(MyShareViewState(pagingData = pager))

}

data class MyShareViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)