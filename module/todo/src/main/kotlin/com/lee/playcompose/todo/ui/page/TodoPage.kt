package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.widget.ActionMode
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.todo.ui.callback.TodoListCallbackHandler
import com.lee.playcompose.todo.ui.callback.rememberCallbackHandler
import com.lee.playcompose.todo.viewmodel.TodoViewAction
import com.lee.playcompose.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/25
 * @description todo页面（待完成/已完成）
 */
@Composable
fun TodoPage(navController: NavController, viewModel: TodoViewModel = viewModel()) {
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val callbackHandler by rememberCallbackHandler(lifecycle = LocalLifecycleOwner.current.lifecycle)
    val viewState = viewModel.viewStates

    SelectTodoTypeDialog(
        isShow = viewState.isShowTypeDialog,
        onDismissRequest = { type ->
            viewModel.dispatch(TodoViewAction.ChangeTypeSelected(type = type))
            viewModel.dispatch(TodoViewAction.ChangeTypeDialogVisible(visible = false))
        }
    )

    AppBarViewContainer(
        title = stringResource(id = viewState.todoTitleRes),
        actionIcon = R.drawable.vector_replace,
        actionMode = ActionMode.Button,
        actionClick = { viewModel.dispatch(TodoViewAction.ChangeTypeDialogVisible(visible = true)) },
        navigationClick = {
            navController.popBackStack()
        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            TodoContent(
                navController = navController,
                type = viewState.type,
                pagerState = pagerState,
                callbackHandler = callbackHandler,
                onCreateClick = {
                    navController.navigateArgs(RoutePage.Todo.CreateTodo.route)
                }
            )

            BottomNavigation(backgroundColor = AppTheme.colors.item) {
                tabItems.forEachIndexed { index, item ->
                    val isSelect = pagerState.currentPage == index
                    BottomNavigationItem(selected = isSelect, icon = {
                        NavigationIcon(isSelected = isSelect, item = item)
                    }, onClick = {
                        coroutine.launch { pagerState.scrollToPage(index) }
                    })
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.TodoContent(
    navController: NavController,
    @TodoType type: Int,
    pagerState: PagerState,
    callbackHandler: TodoListCallbackHandler,
    onCreateClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        HorizontalPager(
            count = tabItems.size,
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            TodoListPage(
                navController = navController,
                type = type,
                status = page,
                callbackHandler = callbackHandler
            )
        }

        FloatingActionButton(
            onClick = { onCreateClick() },
            backgroundColor = AppTheme.colors.focus,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(OffsetLarge)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.vector_create),
                tint = Color.White,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun NavigationIcon(isSelected: Boolean, item: TodoTab) {
    val icon = if (isSelected) item.selectIcon else item.icon
    val color = if (isSelected) AppTheme.colors.focus else AppTheme.colors.primary
    Icon(painterResource(id = icon), null, tint = color)
}

private val tabItems = listOf(TodoTab.Upcoming, TodoTab.Complete)

private sealed class TodoTab(val route: String, val icon: Int, val selectIcon: Int) {
    object Upcoming :
        TodoTab("Upcoming", R.drawable.vector_upcoming, R.drawable.vector_upcoming_fill)

    object Complete :
        TodoTab("Complete", R.drawable.vector_complete, R.drawable.vector_complete_fill)

}