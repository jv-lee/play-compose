package com.lee.playcompose.todo.ui.page

import android.app.DatePickerDialog
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.onTap
import com.lee.playcompose.base.extensions.rememberImePaddingValue
import com.lee.playcompose.common.entity.RefreshTodoListEvent
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.HorizontallySpacer
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeMedium
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.ui.theme.TodoEditContentHeight
import com.lee.playcompose.todo.ui.theme.TodoEditHeight
import com.lee.playcompose.todo.ui.theme.TodoSaveButton
import com.lee.playcompose.todo.viewmodel.CreateTodoViewAction
import com.lee.playcompose.todo.viewmodel.CreateTodoViewEvent
import com.lee.playcompose.todo.viewmodel.CreateTodoViewModel
import com.lee.playcompose.todo.viewmodel.CreateTodoViewState
import kotlinx.coroutines.flow.collect
import java.util.*
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/4/7
 * @description 创建todo/编辑todo页面
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateTodoPage(
    navController: NavController,
    todoData: TodoData? = null,
    viewModel: CreateTodoViewModel = viewModel(factory = CreateTodoViewModel.CreateFactory(todoData))
) {
    val imePadding = rememberImePaddingValue()
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = viewModel.viewStates
    val datePickerDialog = rememberDatePickerDialog(
        activity = LocalActivity.current,
        calendar = viewState.calendar,
        onDateSetListener = viewState.onDateSetListener
    )

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is CreateTodoViewEvent.RequestSuccess -> {
                    ChannelBus.getChannel<RefreshTodoListEvent>()?.send(RefreshTodoListEvent())
                    navController.popBackStack()
                }
                is CreateTodoViewEvent.RequestFailed -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    AppBarViewContainer(
        title = stringResource(id = viewState.appTitleRes),
        navigationClick = {
            keyboardController?.hide()
            navController.popBackStack()
        }) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = imePadding.dp)
            .onTap { keyboardController?.hide() }) {
            CreateTodoContent(
                viewState = viewState,
                changeTitle = { viewModel.dispatch(CreateTodoViewAction.ChangeTitle(it)) },
                changeContent = { viewModel.dispatch(CreateTodoViewAction.ChangeContent(it)) },
                changePriority = { viewModel.dispatch(CreateTodoViewAction.ChangePriority(it)) },
                dateClick = { datePickerDialog.show() })
            CreateTodoBottomButton(saveClick = {
                keyboardController?.hide()
                viewModel.dispatch(CreateTodoViewAction.RequestPostTodo)
            })
        }
    }
}

@Composable
private fun ColumnScope.CreateTodoContent(
    viewState: CreateTodoViewState,
    changeTitle: (String) -> Unit,
    changeContent: (String) -> Unit,
    changePriority: (Int) -> Unit,
    dateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .height(TodoEditHeight)
                .padding(start = OffsetLarge, end = OffsetLarge)
                .wrapContentHeight(align = Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_title_label),
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
                modifier = Modifier
                    .height(TodoEditHeight)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            AppTextField(
                value = viewState.title,
                onValueChange = changeTitle,
                singleLine = true,
                hintText = stringResource(id = R.string.todo_create_title_hint),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.weight(1f)
            )
        }
        HorizontallySpacer(AppTheme.colors.onFocus)
        Row(
            modifier = Modifier
                .height(TodoEditContentHeight)
                .padding(start = OffsetLarge, end = OffsetLarge)
                .wrapContentHeight(align = Alignment.Top)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_content_label),
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
                modifier = Modifier
                    .height(TodoEditHeight)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            AppTextField(
                value = viewState.content,
                singleLine = false,
                maxLine = 5,
                onValueChange = changeContent,
                hintText = stringResource(id = R.string.todo_create_content_hint),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 3.dp)
            )
        }
        HorizontallySpacer(AppTheme.colors.onFocus)
        Row(
            modifier = Modifier
                .height(TodoEditHeight)
                .padding(start = OffsetLarge, end = OffsetLarge)
                .wrapContentHeight(align = Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_level_label),
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
            RadioButton(
                selected = viewState.priority == TodoData.PRIORITY_LOW,
                onClick = { changePriority(TodoData.PRIORITY_LOW) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = AppTheme.colors.accent,
                    unselectedColor = AppTheme.colors.primary
                )
            )
            Text(
                text = stringResource(id = R.string.todo_create_level_low),
                fontSize = FontSizeMedium,
                color = AppTheme.colors.primary,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
            RadioButton(
                selected = viewState.priority == TodoData.PRIORITY_HIGH,
                onClick = { changePriority(TodoData.PRIORITY_HIGH) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = AppTheme.colors.accent,
                    unselectedColor = AppTheme.colors.primary
                )
            )
            Text(
                text = stringResource(id = R.string.todo_create_level_high),
                fontSize = FontSizeMedium,
                color = AppTheme.colors.primary,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
        }
        HorizontallySpacer(AppTheme.colors.onFocus)
        Row(
            modifier = Modifier
                .height(TodoEditHeight)
                .clickable { dateClick() }
                .padding(start = OffsetLarge, end = OffsetLarge)
                .wrapContentHeight(align = Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_date_label),
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
            )
            Text(
                text = viewState.date,
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = CR.drawable.vector_arrow),
                contentDescription = null,
            )
        }
        HorizontallySpacer(AppTheme.colors.onFocus)
    }
}

@Composable
private fun CreateTodoBottomButton(saveClick: () -> Unit) {
    Button(
        onClick = { saveClick() },
        shape = RoundedCornerShape(OffsetRadiusMedium),
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.focus),
        modifier = Modifier
            .padding(OffsetLarge)
            .fillMaxWidth()
            .height(TodoSaveButton)
    ) {
        Text(
            text = stringResource(id = R.string.todo_create_save),
            fontSize = FontSizeMedium,
            color = Color.White
        )
    }
}

@Composable
private fun rememberDatePickerDialog(
    activity: FragmentActivity,
    calendar: Calendar,
    onDateSetListener: DatePickerDialog.OnDateSetListener?
): DatePickerDialog {
    val datePicker = remember {
        DatePickerDialog(
            activity, onDateSetListener, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    DisposableEffect(datePicker) {
        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                datePicker.setOnDateSetListener(null)
            }
        }
    }
    return datePicker
}