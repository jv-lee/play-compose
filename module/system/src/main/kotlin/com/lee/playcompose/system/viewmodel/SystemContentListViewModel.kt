package com.lee.playcompose.system.viewmodel

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
import com.lee.playcompose.system.model.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * 体系内容子页面contentList viewModel
 * @author jv.lee
 * @date 2022/3/8
 */
class SystemContentListViewModel(private val id: Long) : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        pager { page ->
            if (page == 0) delay(300)
            api.getContentDataAsync(page, id).checkData()
        }
    }

    var viewStates by mutableStateOf(SystemContentListViewState(pagingData = pager))
        private set

    class CreateFactory(private val id: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Long::class.java).newInstance(id)
        }
    }
}

data class SystemContentListViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)