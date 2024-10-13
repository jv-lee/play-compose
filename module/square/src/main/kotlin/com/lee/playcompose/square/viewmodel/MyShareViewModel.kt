package com.lee.playcompose.square.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.lowestTime
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.square.R
import com.lee.playcompose.square.model.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 我的分享内容页viewModel
 * @author jv.lee
 * @date 2022/3/29
 */
class MyShareViewModel : BaseMVIViewModel<MyShareViewState, MyShareViewEvent, MyShareViewIntent>() {

    private val api = createApi<ApiService>()
    private val accountService: AccountService = ModuleService.find()

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<Content>())
    private val removedItemsFlow: Flow<MutableList<Content>> get() = _removedItemsFlow

    private val deleteLock = AtomicBoolean(false)

    private val pager by lazy {
        savedPager(
            savedKey = javaClass.simpleName.plus(accountService.getUserId()),
            initialKey = 1,
            removedFlow = removedItemsFlow
        ) {
            api.getMyShareDataSync(it).checkData().shareArticles
        }
    }
    
    init {
        _viewStates = _viewStates.copy(savedPager = pager)
    }

    override fun initViewState() = MyShareViewState()

    override fun dispatch(intent: MyShareViewIntent) {
        when (intent) {
            is MyShareViewIntent.RequestDeleteShare -> {
                deleteShare(intent.content)
            }
        }
    }

    private fun deleteShare(content: Content) {
        if (deleteLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    emit(api.postDeleteShareAsync(content.id).checkData())
                }.onStart {
                    _viewStates = _viewStates.copy(isLoading = true)
                }.catch { error ->
                    _viewEvents.send(MyShareViewEvent.DeleteShareEvent(error.message))
                }.onCompletion {
                    deleteLock.set(false)
                    _viewStates = _viewStates.copy(isLoading = false)
                }.lowestTime().collect {
                    itemsDelete(content)
                    _viewEvents.send(
                        MyShareViewEvent.DeleteShareEvent(
                            app.getString(R.string.share_delete_success)
                        )
                    )
                }
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
    val isLoading: Boolean = false,
    val savedPager: SavedPager<Content>? = null,
    val listState: LazyListState = LazyListState()
) : IViewState

sealed class MyShareViewEvent : IViewEvent {
    data class DeleteShareEvent(val message: String?) : MyShareViewEvent()
}

sealed class MyShareViewIntent : IViewIntent {
    data class RequestDeleteShare(val content: Content) : MyShareViewIntent()
}