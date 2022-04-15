package com.lee.playcompose.me.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.extensions.savedPager
import com.lee.playcompose.me.R
import com.lee.playcompose.me.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/30
 * @description
 */
class CollectViewModel : ViewModel() {
    private val api = createApi<ApiService>()

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<Content>())
    private val removedItemsFlow: Flow<MutableList<Content>> get() = _removedItemsFlow

    private val pager by lazy {
        savedPager { api.getCollectListAsync(it).checkData() }
            // 添加被移除的数据过滤逻辑
            .combine(removedItemsFlow) { pagingData, removed ->
                pagingData.filter { it !in removed }
            }
    }

    var viewStates by mutableStateOf(CollectViewState(pagingData = pager))
        private set

    private val _viewEvents = Channel<CollectViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: CollectViewAction) {
        when (action) {
            is CollectViewAction.RequestUnCollect -> {
                deleteCollect(action.content)
            }
        }
    }

    private fun deleteCollect(content: Content) {
        viewModelScope.launch {
            flow {
                emit(api.postUnCollectAsync(content.id, content.originId).checkData())
            }.catch { error ->
                _viewEvents.send(CollectViewEvent.UnCollectEvent(error.message))
            }.collect {
                itemsDelete(content)
                _viewEvents.send(CollectViewEvent.UnCollectEvent(app.getString(R.string.collect_remove_item_success)))
            }
        }
    }

    /**
     * paging3 移除数据处理
     */
    private fun itemsDelete(item: Content) {
        val removes = _removedItemsFlow.value
        val list = mutableListOf(item)
        list.addAll(removes)
        _removedItemsFlow.value = list
    }

}

data class CollectViewState(
    val pagingData: Flow<PagingData<Content>>,
    val listState: LazyListState = LazyListState()
)

sealed class CollectViewEvent {
    data class UnCollectEvent(val message: String?) : CollectViewEvent()
}

sealed class CollectViewAction {
    data class RequestUnCollect(val content: Content) : CollectViewAction()
}