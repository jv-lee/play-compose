package com.lee.playcompose.system.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.extensions.localPager
import com.lee.playcompose.system.model.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 体系内容 viewModel
 */
class SystemContentListViewModel(private val id: Long) : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        localPager(remoteKey = this.javaClass.simpleName.plus(id)) { page ->
            if (page == 0) delay(300)
            api.getContentDataAsync(page, id).checkData().data
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