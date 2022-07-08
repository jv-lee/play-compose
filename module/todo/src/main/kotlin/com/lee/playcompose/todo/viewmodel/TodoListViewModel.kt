package com.lee.playcompose.todo.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.getCache
import com.lee.playcompose.base.extensions.putCache
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.todo.model.api.ApiService
import com.lee.playcompose.todo.ui.page.STATUS_COMPLETE
import com.lee.playcompose.todo.ui.page.STATUS_UPCOMING
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * TodoViewModel TodoList页面使用，删改查处理
 * @author jv.lee
 * @date 2022/4/6
 */
class TodoListViewModel(private val type: Int, private val status: Int) : ViewModel() {

    private val api = createApi<ApiService>()
    private val accountService: AccountService = ModuleService.find()
    private val cacheManager = CacheManager.getDefault()

    private val saveKey = javaClass.simpleName
        .plus(accountService.getUserId()).plus(type).plus(status)

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<TodoData>())
    private val removedItemsFlow: Flow<MutableList<TodoData>> get() = _removedItemsFlow

    private val pager by lazy {
        savedPager(
            initialKey = 1,
            savedKey = saveKey,
            removedFlow = removedItemsFlow
        ) { page ->
            if (page == 1) clearRemoveItems()
            api.postTodoDataAsync(page, type, status).checkData()
        }
    }

    var viewStates by mutableStateOf(TodoListViewState(savedPager = pager))
        private set

    private val _viewEvents = Channel<TodoListViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: TodoListViewAction) {
        when (action) {
            is TodoListViewAction.RequestDeleteTodo -> {
                requestDeleteTodo(action.todoData)
            }
            is TodoListViewAction.RequestUpdateTodoStatus -> {
                requestUpdateTodoStatus(action.todoData)
            }
        }
    }

    private fun requestDeleteTodo(todoData: TodoData) {
        viewModelScope.launch {
            flow {
                emit(api.postDeleteTodoAsync(todoData.id).checkData())
            }.onStart {
                _viewEvents.send(TodoListViewEvent.ResetSlidingState)
                itemsDelete(todoData)
            }.catch { error ->
                itemsInsert(todoData)
                _viewEvents.send(TodoListViewEvent.RequestFailed(error.message))
            }.collect {
                // 刷新非当前状态页面数据（本页面数据本地操作）
                val statusKey = if (status == STATUS_UPCOMING) STATUS_COMPLETE else STATUS_UPCOMING
                _viewEvents.send(TodoListViewEvent.RefreshTodoData(statusKey.toString()))
            }
        }
    }

    private fun requestUpdateTodoStatus(todoData: TodoData) {
        viewModelScope.launch {
            flow {
                val newItem =
                    todoData.copy(status = if (status == STATUS_UPCOMING) STATUS_COMPLETE else STATUS_UPCOMING)
                emit(api.postUpdateTodoStatusAsync(newItem.id, newItem.status).checkData())
            }.onStart {
                _viewEvents.send(TodoListViewEvent.ResetSlidingState)
                itemsDelete(todoData)
            }.catch { error ->
                itemsInsert(todoData)
                _viewEvents.send(TodoListViewEvent.RequestFailed(error.message))
            }.collect {
                // 刷新非当前状态页面数据（本页面数据本地操作）
                val statusKey = if (status == STATUS_UPCOMING) STATUS_COMPLETE else STATUS_UPCOMING
                _viewEvents.send(TodoListViewEvent.RefreshTodoData(statusKey.toString()))
            }
        }
    }

    /**
     * 被移除过滤的item数据项 移除数据处理
     */
    private suspend fun itemsDelete(item: TodoData) {
        // 延迟处理先让view恢复状态后移除
        delay(300)
        val removes = _removedItemsFlow.value
        val list = mutableListOf(item)
        list.addAll(removes)
        _removedItemsFlow.value = list
        updateCache(item, false)
    }

    /**
     * 被移除过滤的item数据项 添加数据处理
     */
    private fun itemsInsert(item: TodoData) {
        val removes = _removedItemsFlow.value
        removes.remove(item)
        _removedItemsFlow.value = removes
        updateCache(item, true)
    }

    /**
     * 被移除过滤的item数据项 移除所有被删除item重新加载时使用
     */
    private fun clearRemoveItems() {
        _removedItemsFlow.value = mutableListOf()
    }

    /**
     * 本地分页缓存处理 (因当前页面移除item网络请求后不做网络请求刷新只做本地处理特此更新缓存，下次进入页面即时响应缓存)
     */
    private fun updateCache(item: TodoData, isAdd: Boolean) {
        val cacheData = cacheManager.getCache<PageData<TodoData>>(saveKey) ?: return
        if (isAdd) {
            cacheData.data = cacheData.data.toMutableList().apply { add(item) }
        } else {
            cacheData.data = cacheData.data.toMutableList().apply { remove(item) }
        }
        cacheManager.putCache(saveKey, cacheData)
    }

    class CreateFactory(private val type: Int, private val status: Int) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Int::class.java, Int::class.java)
                .newInstance(type, status)
        }
    }
}

data class TodoListViewState(
    val savedPager: SavedPager<TodoData>,
    val listState: LazyListState = LazyListState()
)

sealed class TodoListViewEvent {
    object ResetSlidingState : TodoListViewEvent()
    data class RefreshTodoData(val statusKey: String) : TodoListViewEvent()
    data class RequestFailed(val message: String?) : TodoListViewEvent()
}

sealed class TodoListViewAction {
    data class RequestDeleteTodo(val todoData: TodoData) : TodoListViewAction()
    data class RequestUpdateTodoStatus(val todoData: TodoData) : TodoListViewAction()
}