package com.lee.playcompose.project.viewmodel

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
import com.lee.playcompose.project.model.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/11
 * @description
 */
class ProjectListViewModel(private val id: Long) : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        localPager(initialKey = 1, remoteKey = this.javaClass.simpleName.plus(id)) { page ->
            if (page == 1) delay(300)
            api.getProjectDataAsync(page, id).checkData().data
        }
    }

    var viewStates by mutableStateOf(ProjectListViewState(pager))
        private set

    class CreateFactory(private val id: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Long::class.java).newInstance(id)
        }
    }

}

data class ProjectListViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)