package com.lee.playcompose.todo.viewmodel

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.extensions.lowestTime
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.base.utils.TimeUtil
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.constants.Constants.SP_KEY_TODO_TYPE
import com.lee.playcompose.todo.model.api.ApiService
import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.todo.ui.page.STATUS_UPCOMING
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建todo内容页面viewModel
 * @author jv.lee
 * @date 2022/4/8
 */
class CreateTodoViewModel(private val todoData: TodoData?) :
    ViewModel(),
    DatePickerDialog.OnDateSetListener {

    private val api = createApi<ApiService>()

    private val accountService: AccountService = ModuleService.find()

    private val typeSavedKey = SP_KEY_TODO_TYPE.plus(accountService.getUserId())

    var viewStates by mutableStateOf(
        CreateTodoViewState(type = PreferencesTools.get(typeSavedKey, TodoType.DEFAULT))
    )
        private set

    private val _viewEvents = Channel<CreateTodoViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        initPageState()
    }

    fun dispatch(intent: CreateTodoViewIntent) {
        when (intent) {
            is CreateTodoViewIntent.ChangeTitle -> {
                changeTitle(intent.title)
            }
            is CreateTodoViewIntent.ChangeContent -> {
                changeContent(intent.content)
            }
            is CreateTodoViewIntent.ChangePriority -> {
                changePriority(intent.priority)
            }
            is CreateTodoViewIntent.ChangeDate -> {
                changeDate(intent.date)
            }
            is CreateTodoViewIntent.RequestPostTodo -> {
                requestPostTodo()
            }
        }
    }

    private fun initPageState() {
        val dateStr = todoData?.dateStr ?: dateToStrFormat()
        viewStates = viewStates.copy(
            appTitleRes = if (todoData == null) R.string.title_create_todo
            else R.string.title_edit_todo,
            isCreate = todoData == null,
            title = todoData?.title ?: "",
            content = todoData?.content ?: "",
            priority = todoData?.priority ?: TodoData.PRIORITY_LOW,
            date = todoData?.dateStr ?: dateToStrFormat(),
            calendar = stringToCalendar(dateStr),
            onDateSetListener = this
        )
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

    private fun requestPostTodo() {
        if (viewStates.isCreate) requestAddTodo() else requestUpdateTodo()
    }

    private fun requestAddTodo() {
        viewModelScope.launch {
            flow {
                val response = api.postAddTodoAsync(
                    viewStates.title,
                    viewStates.content,
                    viewStates.date,
                    viewStates.type,
                    viewStates.priority
                ).checkData()
                emit(response)
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                _viewEvents.send(CreateTodoViewEvent.RequestFailed(error.message))
            }.onCompletion {
                viewStates = viewStates.copy(isLoading = false)
            }.lowestTime().collect {
                _viewEvents.send(CreateTodoViewEvent.RequestSuccess(STATUS_UPCOMING))
            }
        }
    }

    private fun requestUpdateTodo() {
        viewModelScope.launch {
            flow {
                val response = api.postUpdateTodoAsync(
                    todoData?.id ?: 0,
                    viewStates.title,
                    viewStates.content,
                    viewStates.date,
                    viewStates.type,
                    viewStates.priority,
                    todoData?.status ?: STATUS_UPCOMING
                ).checkData()
                emit(response)
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                _viewEvents.send(CreateTodoViewEvent.RequestFailed(error.message))
            }.onCompletion {
                viewStates = viewStates.copy(isLoading = false)
            }.lowestTime().collect {
                _viewEvents.send(
                    CreateTodoViewEvent.RequestSuccess(
                        todoData?.status ?: STATUS_UPCOMING
                    )
                )
            }
        }
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
        dispatch(CreateTodoViewIntent.ChangeDate(date))
    }

    class CreateFactory(private val todoData: TodoData?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(TodoData::class.java).newInstance(todoData)
        }
    }
}

data class CreateTodoViewState(
    var type: Int = TodoType.DEFAULT,
    val isLoading: Boolean = false,
    val appTitleRes: Int = R.string.title_create_todo,
    val isCreate: Boolean = true,
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val calendar: Calendar = Calendar.getInstance(),
    val priority: Int = TodoData.PRIORITY_LOW,
    val onDateSetListener: DatePickerDialog.OnDateSetListener? = null
)

sealed class CreateTodoViewEvent {
    data class RequestSuccess(val status: Int) : CreateTodoViewEvent()
    data class RequestFailed(val message: String?) : CreateTodoViewEvent()
}

sealed class CreateTodoViewIntent {
    data class ChangeTitle(val title: String) : CreateTodoViewIntent()
    data class ChangeContent(val content: String) : CreateTodoViewIntent()
    data class ChangePriority(val priority: Int) : CreateTodoViewIntent()
    data class ChangeDate(val date: String) : CreateTodoViewIntent()
    object RequestPostTodo : CreateTodoViewIntent()
}