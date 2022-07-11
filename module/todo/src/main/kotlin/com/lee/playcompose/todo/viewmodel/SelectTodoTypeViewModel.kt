package com.lee.playcompose.todo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.ui.widget.PagerSnapState
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.todo.model.entity.TodoTypeData
import com.lee.playcompose.todo.model.entity.TodoTypeWheelData
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * 选择todoType弹窗viewModel
 * @author jv.lee
 * @date 2022/1/2
 */
class SelectTodoTypeViewModel : ViewModel() {

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    var viewStates by mutableStateOf(SelectTodoTypeViewState())
        private set

    init {
        requestTodoTypes()
    }

    private fun requestTodoTypes() {
        viewModelScope.launch {
            flow {
                val type = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT)
                val data = TodoTypeData.getTodoTypes()
                emit(TodoTypeWheelData(type, data))
            }.collect {
                viewStates = viewStates.copy(todoTypes = it.todoTypes)
                viewStates.state.currentIndex = it.startIndex
            }
        }
    }

}

data class SelectTodoTypeViewState(
    val state: PagerSnapState = PagerSnapState(),
    val todoTypes: MutableList<TodoTypeData> = mutableListOf()
)