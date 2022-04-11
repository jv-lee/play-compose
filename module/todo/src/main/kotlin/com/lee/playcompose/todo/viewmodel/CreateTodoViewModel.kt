package com.lee.playcompose.todo.viewmodel

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.playcompose.base.utils.TimeUtil
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.todo.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author jv.lee
 * @date 2022/4/8
 * @description
 */
class CreateTodoViewModel(private val todoData: TodoData?) : ViewModel(),
    DatePickerDialog.OnDateSetListener {

    var viewStates by mutableStateOf(CreateTodoViewState())
        private set

    init {
        initPageState()
    }

    fun dispatch(action: CreateTodoViewAction) {
        when (action) {
            is CreateTodoViewAction.ChangeTitle -> {
                changeTitle(action.title)
            }
            is CreateTodoViewAction.ChangeContent -> {
                changeContent(action.content)
            }
            is CreateTodoViewAction.ChangePriority -> {
                changePriority(action.priority)
            }
            is CreateTodoViewAction.ChangeDate -> {
                changeDate(action.date)
            }
        }
    }

    private fun changeTitle(title: String) {
        viewStates = viewStates.copy(title = title)
    }

    private fun changeContent(content: String) {
        viewStates = viewStates.copy(content = content)
    }

    private fun changePriority(priority: Int) {
        viewStates = viewStates.copy(priority = priority)
    }

    private fun changeDate(date: String) {
        viewStates = viewStates.copy(date = date, calendar = stringToCalendar(date))
    }

    private fun initPageState() {
        val dateStr = todoData?.dateStr ?: dateToStrFormat()
        viewStates = viewStates.copy(
            appTitleRes = if (todoData == null) R.string.title_create_todo else R.string.title_edit_todo,
            isCreate = todoData == null,
            title = todoData?.title ?: "",
            content = todoData?.content ?: "",
            priority = todoData?.priority ?: TodoData.PRIORITY_LOW,
            date = todoData?.dateStr ?: dateToStrFormat(),
            calendar = stringToCalendar(dateStr),
            onDateSetListener = this,
        )
    }

    private fun stringToCalendar(dateStr: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr)!!
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    private fun dateToStrFormat(): String {
        return TimeUtil.date2String(Date(), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$year-${month + 1}-$dayOfMonth"
        dispatch(CreateTodoViewAction.ChangeDate(date))
    }

    class CreateFactory(private val todoData: TodoData?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(TodoData::class.java).newInstance(todoData)
        }
    }

}

data class CreateTodoViewState(
    val appTitleRes: Int = R.string.title_create_todo,
    val isCreate: Boolean = true,
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val calendar: Calendar = Calendar.getInstance(),
    val priority: Int = TodoData.PRIORITY_LOW,
    val onDateSetListener: DatePickerDialog.OnDateSetListener? = null
)

sealed class CreateTodoViewAction {
    data class ChangeTitle(val title: String) : CreateTodoViewAction()
    data class ChangeContent(val content: String) : CreateTodoViewAction()
    data class ChangePriority(val priority: Int) : CreateTodoViewAction()
    data class ChangeDate(val date: String) : CreateTodoViewAction()
}