package com.lee.playcompose.todo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.ui.widget.PagerSnapState
import com.lee.playcompose.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.todo.model.entity.TodoTypeData
import com.lee.playcompose.todo.model.entity.TodoTypeWheelData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/1/2
 * @description
 */
class SelectTodoTypeViewModel : ViewModel() {

    var viewStates by mutableStateOf(SelectTodoTypeViewState())
        private set

    init {
        requestTodoTypes()
    }

    private fun requestTodoTypes() {
        viewModelScope.launch {
            flow {
                val type = PreferencesTools.get(SP_KEY_TODO_TYPE, TodoType.DEFAULT)
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