package com.lee.playcompose.square.viewmodel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.extensions.pager
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
        pager {
            api.getSquareDataSync(it) }.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(SquareViewState(pagingData = pager))
        private set

    init {
        Log.i("jv.lee","create Square")
    }
}

data class SquareViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)