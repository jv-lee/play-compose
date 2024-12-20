package com.lee.playcompose.todo.ui.page

import android.app.DatePickerDialog
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalActivity
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.base.ktx.activityViewModel
import com.lee.playcompose.base.ktx.onTap
import com.lee.playcompose.base.ktx.setResult
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.ktx.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.HorizontallySpacer
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.viewmodel.ThemeViewModel
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.ui.theme.TodoEditContentHeight
import com.lee.playcompose.todo.ui.theme.TodoEditHeight
import com.lee.playcompose.todo.ui.theme.TodoSaveButton
import com.lee.playcompose.todo.viewmodel.CreateTodoViewEvent
import com.lee.playcompose.todo.viewmodel.CreateTodoViewIntent
import com.lee.playcompose.todo.viewmodel.CreateTodoViewModel
import com.lee.playcompose.todo.viewmodel.CreateTodoViewState
import java.util.Calendar
import com.lee.playcompose.common.R as CR

/**
 * 创建todo/编辑todo页面
 * @author jv.lee
 * @date 2022/4/7
 */
@Composable
fun CreateTodoPage(
    todoData: TodoData? = null,
    navController: NavController = LocalNavController.current,
    viewModel: CreateTodoViewModel = viewModel(
        factory = CreateTodoViewModel.CreateFactory(todoData)
    )
) {
    val focusManager = LocalFocusManager.current
    val viewState = viewModel.viewStates()
    val datePickerDialog = rememberDatePickerDialog(
        activity = LocalActivity.current,
        calendar = viewState.calendar,
        onDateSetListener = viewState.onDateSetListener
    )

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is CreateTodoViewEvent.RequestSuccess -> {
                    navController.setResult(REQUEST_KEY_REFRESH, event.status)
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
            focusManager.clearFocus()
            navController.popBackStack()
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .onTap { focusManager.clearFocus() }
            ) {
                CreateTodoContent(
                    viewState = viewState,
                    changeTitle = { viewModel.dispatch(CreateTodoViewIntent.ChangeTitle(it)) },
                    changeContent = { viewModel.dispatch(CreateTodoViewIntent.ChangeContent(it)) },
                    changePriority = { viewModel.dispatch(CreateTodoViewIntent.ChangePriority(it)) },
                    dateClick = { datePickerDialog.show() }
                )
                CreateTodoBottomButton(saveClick = {
                    focusManager.clearFocus()
                    viewModel.dispatch(CreateTodoViewIntent.RequestPostTodo)
                })
            }
        }
    )
}

@Composable
private fun ColumnScope.CreateTodoContent(
    viewState: CreateTodoViewState,
    changeTitle: (String) -> Unit,
    changeContent: (String) -> Unit,
    changePriority: (Int) -> Unit,
    dateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(TodoEditHeight)
                .padding(start = OffsetLarge, end = OffsetLarge)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_title_label),
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.accent,
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
        HorizontallySpacer(ColorsTheme.colors.onFocus)
        Row(
            modifier = Modifier
                .height(TodoEditContentHeight)
                .padding(start = OffsetLarge, end = OffsetLarge)
                .wrapContentHeight(align = Alignment.Top)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_content_label),
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.accent,
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
                modifier = Modifier.weight(1f)
            )
        }
        HorizontallySpacer(ColorsTheme.colors.onFocus)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(TodoEditHeight)
                .padding(start = OffsetLarge, end = OffsetLarge)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_level_label),
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.accent,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
            RadioButton(
                selected = viewState.priority == TodoData.PRIORITY_LOW,
                onClick = { changePriority(TodoData.PRIORITY_LOW) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = ColorsTheme.colors.accent,
                    unselectedColor = ColorsTheme.colors.primary
                )
            )
            Text(
                text = stringResource(id = R.string.todo_create_level_low),
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.primary,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
            RadioButton(
                selected = viewState.priority == TodoData.PRIORITY_HIGH,
                onClick = { changePriority(TodoData.PRIORITY_HIGH) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = ColorsTheme.colors.accent,
                    unselectedColor = ColorsTheme.colors.primary
                )
            )
            Text(
                text = stringResource(id = R.string.todo_create_level_high),
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.primary,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
        }
        HorizontallySpacer(ColorsTheme.colors.onFocus)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(TodoEditHeight)
                .clickable { dateClick() }
                .padding(start = OffsetLarge, end = OffsetLarge)
        ) {
            Text(
                text = stringResource(id = R.string.todo_create_date_label),
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.accent
            )
            Text(
                text = viewState.date,
                fontSize = FontSizeTheme.sizes.medium,
                color = ColorsTheme.colors.accent,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = CR.drawable.vector_arrow),
                contentDescription = null,
                tint = ColorsTheme.colors.accent
            )
        }
        HorizontallySpacer(ColorsTheme.colors.onFocus)
    }
}

@Composable
private fun CreateTodoBottomButton(saveClick: () -> Unit) {
    Button(
        onClick = { saveClick() },
        shape = RoundedCornerShape(OffsetRadiusMedium),
        colors = ButtonDefaults.buttonColors(containerColor = ColorsTheme.colors.focus),
        modifier = Modifier
            .padding(OffsetLarge)
            .fillMaxWidth()
            .height(TodoSaveButton)
    ) {
        Text(
            text = stringResource(id = R.string.todo_create_save),
            fontSize = FontSizeTheme.sizes.medium,
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
    val viewModel: ThemeViewModel = activityViewModel(LocalActivity.current)
    val theme =
        if (viewModel.viewStates.isDark) CR.style.ThemeNightDatePickerDialog
        else CR.style.ThemeDatePickerDialog
    val datePicker = remember {
        DatePickerDialog(
            activity,
            theme,
            onDateSetListener,
            calendar.get(Calendar.YEAR),
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