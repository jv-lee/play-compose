package com.lee.playcompose.official.viewmodel

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
import com.lee.playcompose.official.model.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * 公众号列表 ViewModel
 * @author jv.lee
 * @date 2022/3/11
 */
class OfficialListViewModel(private val id: Long) : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        pager(initialKey = 1) { page ->
            if (page == 1) delay(300)
            api.getOfficialDataAsync(id, page).checkData()
        }
    }

    var viewStates by mutableStateOf(OfficialListViewState(pager))
        private set

    class CreateFactory(private val id: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Long::class.java).newInstance(id)
        }
    }
}

data class OfficialListViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)