package com.lee.playcompose.todo.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.ktx.getCache
import com.lee.playcompose.base.ktx.putCache
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.todo.model.api.ApiService
import com.lee.playcompose.todo.ui.page.STATUS_COMPLETE
import com.lee.playcompose.todo.ui.page.STATUS_UPCOMING
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TodoViewModel TodoList页面使用，删改查处理
 * @author jv.lee
 * @date 2022/4/6
 */
class TodoListViewModel(private val type: Int, private val status: Int) :
    BaseMVIViewModel<TodoListViewState, TodoListViewEvent, TodoListViewIntent>() {

    private val api = createApi<ApiService>()
    private val accountService: AccountService = ModuleService.find()
    private val cacheManager = CacheManager.getDefault()

    private val saveKey = javaClass.simpleName
        .plus(accountService.getUserId()).plus(type).plus(status)

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<TodoData>())
    private val removedItemsFlow: Flow<MutableList<TodoData>> get() = _removedItemsFlow

    private val deleteLock = AtomicBoolean(false)
    private val updateLock = AtomicBoolean(false)

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
    
    init {
        _viewStates = _viewStates.copy(savedPager = pager)
    }

    override fun initViewState() = TodoListViewState()

    override fun dispatch(intent: TodoListViewIntent) {
        when (intent) {
            is TodoListViewIntent.RequestDeleteTodo -> {
                requestDeleteTodo(intent.todoData)
            }

            is TodoListViewIntent.RequestUpdateTodoStatus -> {
                requestUpdateTodoStatus(intent.todoData)
            }
        }
    }

    private fun requestDeleteTodo(todoData: TodoData) {
        if (deleteLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    emit(api.postDeleteTodoAsync(todoData.id).checkData())
                }.onStart {
                    _viewEvents.send(TodoListViewEvent.ResetSlidingState)
                    itemsDelete(todoData)
                }.catch { error ->
                    itemsInsert(todoData)
                    _viewEvents.send(TodoListViewEvent.RequestFailed(error.message))
                }.onCompletion {
                    deleteLock.set(false)
                }.collect {
                    // 刷新非当前状态页面数据（本页面数据本地操作）
                    val statusKey =
                        if (status == STATUS_UPCOMING) STATUS_COMPLETE else STATUS_UPCOMING
                    _viewEvents.send(TodoListViewEvent.RefreshTodoData(statusKey.toString()))
                }
            }
        }
    }

    private fun requestUpdateTodoStatus(todoData: TodoData) {
        if (updateLock.compareAndSet(false, true)) {
            viewModelScope.launch {
                flow {
                    val newItem =
                        todoData.copy(
                            status = if (status == STATUS_UPCOMING) STATUS_COMPLETE
                            else STATUS_UPCOMING
                        )
                    emit(api.postUpdateTodoStatusAsync(newItem.id, newItem.status).checkData())
                }.onStart {
                    _viewEvents.send(TodoListViewEvent.ResetSlidingState)
                    itemsDelete(todoData)
                }.catch { error ->
                    itemsInsert(todoData)
                    _viewEvents.send(TodoListViewEvent.RequestFailed(error.message))
                }.onCompletion {
                    updateLock.set(false)
                }.collect {
                    // 刷新非当前状态页面数据（本页面数据本地操作）
                    val statusKey =
                        if (status == STATUS_UPCOMING) STATUS_COMPLETE else STATUS_UPCOMING
                    _viewEvents.send(TodoListViewEvent.RefreshTodoData(statusKey.toString()))
                }
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
    val savedPager: SavedPager<TodoData>? = null,
    val listState: LazyListState = LazyListState()
) : IViewState

sealed class TodoListViewEvent : IViewEvent {
    data object ResetSlidingState : TodoListViewEvent()
    data class RefreshTodoData(val statusKey: String) : TodoListViewEvent()
    data class RequestFailed(val message: String?) : TodoListViewEvent()
}

sealed class TodoListViewIntent : IViewIntent {
    data class RequestDeleteTodo(val todoData: TodoData) : TodoListViewIntent()
    data class RequestUpdateTodoStatus(val todoData: TodoData) : TodoListViewIntent()
}