package com.lee.playcompose.square.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.square.R
import com.lee.playcompose.square.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 我的分享内容页viewModel
 * @author jv.lee
 * @date 2022/3/29
 */
class MyShareViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val accountService: AccountService = ModuleService.find()

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<Content>())
    private val removedItemsFlow: Flow<MutableList<Content>> get() = _removedItemsFlow

    private val pager by lazy {
        savedPager(
            savedKey = javaClass.simpleName.plus(accountService.getUserId()),
            initialKey = 1,
            removedFlow = removedItemsFlow
        ) {
            api.getMyShareDataSync(it).checkData().shareArticles
        }
    }

    var viewStates by mutableStateOf(MyShareViewState(savedPager = pager))

    private val _viewEvents = Channel<MyShareViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: MyShareViewAction) {
        when (action) {
            is MyShareViewAction.RequestDeleteShare -> {
                deleteShare(action.content)
            }
        }
    }

    private fun deleteShare(content: Content) {
        viewModelScope.launch {
            flow {
                emit(api.postDeleteShareAsync(content.id).checkData())
            }.catch { error ->
                _viewEvents.send(MyShareViewEvent.DeleteShareEvent(error.message))
            }.collect {
                itemsDelete(content)
                _viewEvents.send(MyShareViewEvent.DeleteShareEvent(app.getString(R.string.share_delete_success)))
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

data class MyShareViewState(
    val savedPager: SavedPager<Content>,
    val listState: LazyListState = LazyListState()
)

sealed class MyShareViewEvent {
    data class DeleteShareEvent(val message: String?) : MyShareViewEvent()
}

sealed class MyShareViewAction {
    data class RequestDeleteShare(val content: Content) : MyShareViewAction()
}