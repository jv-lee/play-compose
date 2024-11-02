package com.lee.playcompose.me.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.ktx.lowestTime
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.me.R
import com.lee.playcompose.me.model.api.ApiService
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 收藏页面ViewModel
 * @author jv.lee
 * @date 2022/3/30
 */
class CollectViewModel : BaseMVIViewModel<CollectViewState, CollectViewEvent, CollectViewIntent>() {

    private val api = createApi<ApiService>()
    private val accountService: AccountService = ModuleService.find()

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<Content>())
    private val removedItemsFlow: Flow<MutableList<Content>> get() = _removedItemsFlow

    private val deleteLock = AtomicBoolean(false)

    private val pager by lazy {
        savedPager(
            savedKey = javaClass.simpleName.plus(accountService.getUserId()),
            removedFlow = removedItemsFlow
        ) { api.getCollectListAsync(it).checkData() }
    }

    init {
        _viewStates = _viewStates.copy(savedPager = pager)
    }

    override fun initViewState() = CollectViewState()

    override fun dispatch(intent: CollectViewIntent) {
        when (intent) {
            is CollectViewIntent.RequestUnCollect -> {
                deleteCollect(intent.content)
            }
        }
    }

    private fun deleteCollect(content: Content) {
        if (deleteLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    emit(api.postUnCollectAsync(content.id, content.originId).checkData())
                }.onStart {
                    _viewStates = _viewStates.copy(isLoading = true)
                }.catch { error ->
                    _viewEvents.send(CollectViewEvent.UnCollectEvent(error.message))
                }.onCompletion {
                    deleteLock.set(false)
                    _viewStates = _viewStates.copy(isLoading = false)
                }.lowestTime().collect {
                    itemsDelete(content)
                    _viewEvents.send(CollectViewEvent.UnCollectEvent(app.getString(R.string.collect_remove_item_success)))
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

data class CollectViewState(
    val isLoading: Boolean = false,
    val savedPager: SavedPager<Content>? = null,
    val listState: LazyListState = LazyListState()
) : IViewState

sealed class CollectViewEvent : IViewEvent {
    data class UnCollectEvent(val message: String?) : CollectViewEvent()
}

sealed class CollectViewIntent : IViewIntent {
    data class RequestUnCollect(val content: Content) : CollectViewIntent()
}