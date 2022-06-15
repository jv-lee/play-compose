package com.lee.playcompose.todo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playcompose.todo.model.entity.TodoType

/**
 * todo页面 viewModel
 * @author jv.lee
 * @date 2022/4/12
 */
class TodoViewModel : ViewModel() {

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    var viewStates by mutableStateOf(TodoViewState())
        private set

    init {
        changePageData()
    }

    fun dispatch(action: TodoViewAction) {
        when (action) {
            is TodoViewAction.ChangeTypeDialogVisible -> {
                changeTypeDialogVisible(action.visible)
            }
            is TodoViewAction.ChangeTypeSelected -> {
                changeTypeSelected(action.type)
            }
        }
    }

    private fun changePageData(type: Int = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT)) {
        val todoTitleRes = when (type) {
            TodoType.WORK -> R.string.todo_title_work
            TodoType.LIFE -> R.string.todo_title_life
            TodoType.PLAY -> R.string.todo_title_play
            else -> R.string.todo_title_default
        }
        viewStates = viewStates.copy(type = type, todoTitleRes = todoTitleRes)
    }

    private fun changeTypeDialogVisible(visible: Boolean) {
        viewStates = viewStates.copy(isShowTypeDialog = visible)
    }

    private fun changeTypeSelected(@TodoType type: Int) {
        PreferencesTools.put(typeSavedKey, type)
        changePageData(type)
    }

}

data class TodoViewState(
    val isShowTypeDialog: Boolean = false,
    val type: Int = TodoType.DEFAULT,
    val todoTitleRes: Int = R.string.todo_title_default
)

sealed class TodoViewAction {
    data class ChangeTypeSelected(@TodoType val type: Int) : TodoViewAction()
    data class ChangeTypeDialogVisible(val visible: Boolean) : TodoViewAction()
}