package com.lee.playcompose.todo.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    // paging3 移除数据过滤项
    private var _removedItemsFlow = MutableStateFlow(mutableListOf<TodoData>())
    private val removedItemsFlow: Flow<MutableList<TodoData>> get() = _removedItemsFlow

    private val pager by lazy {
        savedPager(
            initialKey = 1,
            savedKey = javaClass.simpleName
                .plus(accountService.getUserId()).plus(type).plus(status),
            removedFlow = removedItemsFlow
        ) { page ->
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
                _viewEvents.send(TodoListViewEvent.ResetSlidingState {
                    itemsDelete(todoData)
                })
            }.catch { error ->
                itemsInsert(todoData)
                _viewEvents.send(TodoListViewEvent.RequestFailed(error.message))
            }.collect {
                clearRemoveItems()
                _viewEvents.send(TodoListViewEvent.RefreshTodoData)
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
                _viewEvents.send(TodoListViewEvent.ResetSlidingState {
                    itemsDelete(todoData)
                })
            }.catch { error ->
                itemsInsert(todoData)
                _viewEvents.send(TodoListViewEvent.RequestFailed(error.message))
            }.collect {
                clearRemoveItems()
                _viewEvents.send(TodoListViewEvent.RefreshTodoData)
            }
        }
    }

    /**
     * 移除数据处理
     */
    private fun itemsDelete(item: TodoData) {
        val removes = _removedItemsFlow.value
        val list = mutableListOf(item)
        list.addAll(removes)
        _removedItemsFlow.value = list
    }

    /**
     * 添加数据处理
     */
    private fun itemsInsert(item: TodoData) {
        val removes = _removedItemsFlow.value
        removes.remove(item)
        _removedItemsFlow.value = removes
    }

    /**
     * 移除所有被删除item重新加载时使用
     */
    private fun clearRemoveItems() {
        _removedItemsFlow.value = mutableListOf()
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
    data class ResetSlidingState(val removeCall: () -> Unit) : TodoListViewEvent()
    object RefreshTodoData : TodoListViewEvent()
    data class RequestFailed(val message: String?) : TodoListViewEvent()
}

sealed class TodoListViewAction {
    data class RequestDeleteTodo(val todoData: TodoData) : TodoListViewAction()
    data class RequestUpdateTodoStatus(val todoData: TodoData) : TodoListViewAction()
}