package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.ui.callback.PageCallbackHandler
import com.lee.playcompose.common.ui.callback.rememberPageCallbackHandler
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.widget.header.ActionMode
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.todo.ui.callback.TodoListCallback
import com.lee.playcompose.todo.ui.dialog.SelectTodoTypeDialog
import com.lee.playcompose.todo.viewmodel.TodoViewIntent
import com.lee.playcompose.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

/**
 * todo页面（待完成/已完成）
 * @author jv.lee
 * @date 2022/3/25
 */
@Composable
fun TodoPage(
    navController: NavController = LocalNavController.current,
    viewModel: TodoViewModel = viewModel()
) {
    val handler by rememberPageCallbackHandler<TodoListCallback>(
        lifecycle = LocalLifecycleOwner.current
    )
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabItems.size })
    val viewState = viewModel.viewStates

    SelectTodoTypeDialog(
        isShow = viewState.isShowTypeDialog,
        onDismissRequest = { type ->
            viewModel.dispatch(TodoViewIntent.ChangeTypeSelected(type = type))
            viewModel.dispatch(TodoViewIntent.ChangeTypeDialogVisible(visible = false))
        }
    )

    AppBarViewContainer(
        title = stringResource(id = viewState.todoTitleRes),
        actionIcon = R.drawable.vector_replace,
        actionMode = ActionMode.Button,
        actionClick = {
            viewModel.dispatch(TodoViewIntent.ChangeTypeDialogVisible(visible = true))
        },
        navigationClick = {
            navController.popBackStack()
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TodoContent(
                type = viewState.type,
                pagerState = pagerState,
                handler = handler,
                onCreateClick = {
                    navController.navigateArgs(RoutePage.Todo.CreateTodo.route)
                }
            )

            BottomNavigation(
                elevation = viewState.navigationElevation,
                backgroundColor = ColorsTheme.colors.item
            ) {
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
    @TodoType type: Int,
    pagerState: PagerState,
    handler: PageCallbackHandler<TodoListCallback>,
    onCreateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            TodoListPage(
                type = type,
                status = page,
                handler = handler
            )
        }

        FloatingActionButton(
            onClick = { onCreateClick() },
            backgroundColor = ColorsTheme.colors.focus,
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
    val color = if (isSelected) ColorsTheme.colors.focus else ColorsTheme.colors.primary
    Icon(painterResource(id = icon), null, tint = color)
}

private val tabItems = listOf(TodoTab.Upcoming, TodoTab.Complete)

private sealed class TodoTab(val route: String, val icon: Int, val selectIcon: Int) {
    object Upcoming :
        TodoTab("Upcoming", R.drawable.vector_upcoming, R.drawable.vector_upcoming_fill)

    object Complete :
        TodoTab("Complete", R.drawable.vector_complete, R.drawable.vector_complete_fill)
}