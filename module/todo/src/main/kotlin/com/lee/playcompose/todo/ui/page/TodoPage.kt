package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.todo.ui.callback.TodoListCallbackHandler
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/25
 * @description todo页面（待完成/已完成）
 */
@Composable
fun TodoPage(navController: NavController) {
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val callbackHandler by remember { mutableStateOf(TodoListCallbackHandler(lifecycle = lifecycle)) }

    AppBarViewContainer(title = stringResource(id = R.string.todo_title_default)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
        ) {
            HorizontalPager(
                count = tabItems.size,
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                TodoListPage(
                    navController = navController,
                    TodoType.DEFAULT,
                    status = page,
                    callbackHandler = callbackHandler
                )
            }

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