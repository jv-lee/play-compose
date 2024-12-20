package com.lee.playcompose.search.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import com.lee.playcompose.common.paging.extensions.pager
import com.lee.playcompose.search.model.api.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * 搜索结果页viewModel
 * @author jv.lee
 * @date 2022/3/18
 */
class SearchResultViewModel(private val key: String) : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        pager { api.postSearchAsync(it, key).checkData() }
    }

    var viewStates by mutableStateOf(SearchResultViewState(pagingData = pager))
        private set

    class CreateFactory(private val key: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(String::class.java).newInstance(key)
        }
    }
}

data class SearchResultViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)