package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.HorizontallySpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.*
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.router.parseRoute
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.constants.Constants.STATUS_UPCOMING
import com.lee.playcompose.todo.ui.callback.TodoListCallback
import com.lee.playcompose.todo.ui.callback.TodoListCallbackHandler
import com.lee.playcompose.todo.ui.theme.*
import com.lee.playcompose.todo.viewmodel.TodoListViewAction
import com.lee.playcompose.todo.viewmodel.TodoListViewEvent
import com.lee.playcompose.todo.viewmodel.TodoListViewModel
import com.lee.playcompose.todo.viewmodel.TodoListViewState
import kotlinx.coroutines.flow.collect
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/4/6
 * @description todo列表页
 * @param type: 0默认 1工作 2生活 3娱乐
 * @param status: 0待完成 1已完成
 */
@Composable
fun TodoListPage(
    navController: NavController,
    type: Int,
    status: Int,
    callbackHandler: TodoListCallbackHandler,
    viewModel: TodoListViewModel = viewModel(
        key = (type + status).toString(),
        factory = TodoListViewModel.CreateFactory(type, status)
    )
) {
    val viewState = viewModel.viewStates
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val slidingPaneState by rememberSlidingPaneState()

    // 监听多页面刷新联动回调
    LaunchedEffect(Unit) {
        callbackHandler.addCallback(object : TodoListCallback {
            override fun refresh() {
                contentList.refresh()
            }
        })
    }

    LaunchedEffect(type, status) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is TodoListViewEvent.RefreshTodoData -> {
                    callbackHandler.dispatchCallback()
                }
                is TodoListViewEvent.RequestFailed -> {
                    slidingPaneState.closeAction()
                    toast(event.message)
                }
                is TodoListViewEvent.ResetSlidingState -> {
                    slidingPaneState.closeAction()
                    event.removeCall()
                }
            }
        }
    }

    TodoListContent(
        viewState = viewState,
        slidingPaneState = slidingPaneState,
        onContentItemClick = {
            navController.navigateArgs(RoutePage.Todo.CreateTodo.route, it)
        }, onDelete = {
            viewModel.dispatch(TodoListViewAction.RequestDeleteTodo(it))
        }, onUpdate = {
            viewModel.dispatch(TodoListViewAction.RequestUpdateTodoStatus(it))
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TodoListContent(
    viewState: TodoListViewState,
    slidingPaneState: SlidingPaneState,
    onContentItemClick: (TodoData) -> Unit,
    onDelete: (TodoData) -> Unit,
    onUpdate: (TodoData) -> Unit,
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        swipeEnable = false,
        lazyPagingItems = contentList,
        listState = listState,
    ) {
        // build todos content item
        val items = contentList.itemSnapshotList.items
        items.forEach { item ->
            // stickyHeader
            if (isFirstGroupItem(items, item)) {
                stickyHeader { TodoStickyHeader(text = item.dateStr, slidingPaneState) }
            }
            item {
                TodoListItem(
                    todoData = item,
                    slidingPaneState = slidingPaneState,
                    onContentItemClick = onContentItemClick,
                    onDelete = onDelete,
                    onUpdate = onUpdate
                )
            }
        }
    }
}

@Composable
private fun TodoStickyHeader(text: String, slidingPaneState: SlidingPaneState) {
    Box(
        modifier = Modifier
            .height(StickyHeaderHeight)
            .fillMaxWidth()
            .slidingPaneState(slidingPaneState)
            .background(AppTheme.colors.onFocus)
            .padding(start = OffsetLarge)
            .wrapContentSize(align = Alignment.CenterStart)
    ) {
        Text(
            text = text,
            fontSize = FontSizeSmall,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.focus
        )
    }
}

@Composable
private fun TodoListItem(
    todoData: TodoData,
    slidingPaneState: SlidingPaneState,
    onContentItemClick: (TodoData) -> Unit,
    onDelete: (TodoData) -> Unit,
    onUpdate: (TodoData) -> Unit,
) {
    SlidingPaneBox(
        slidingWidth = SlidingWidth,
        state = slidingPaneState,
        modifier = Modifier
            .height(TodoItemHeight)
            .fillMaxWidth(),
        sliding = {
            Row {
                Text(
                    text = stringResource(id = CR.string.item_delete),
                    fontSize = FontSizeSmall,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable { onDelete(todoData) }
                        .background(ButtonDeleteColor)
                        .wrapContentSize(Alignment.Center)
                )
                Text(
                    text = stringResource(
                        id = if (todoData.status == STATUS_UPCOMING)
                            R.string.todo_item_complete else R.string.todo_item_upcoming
                    ),
                    fontSize = FontSizeSmall,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable { onUpdate(todoData) }
                        .background(ButtonUpdateColor)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.item)
                .clickable { onContentItemClick(todoData) }
        ) {
            Text(
                text = todoData.title,
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
                modifier = Modifier
                    .padding(start = OffsetLarge, top = OffsetMedium)
                    .align(Alignment.TopStart)
            )
            Text(
                text = todoData.content,
                fontSize = FontSizeMedium,
                color = AppTheme.colors.primary,
                modifier = Modifier
                    .padding(start = OffsetLarge, bottom = OffsetMedium)
                    .align(Alignment.BottomStart)
            )
            HorizontallySpacer(color = AppTheme.colors.background)
        }
    }
}

/**
 * 是否是第一个分组key
 */
private fun isFirstGroupItem(list: List<TodoData>, item: TodoData): Boolean {
    val index = list.indexOf(item)
    if (index == 0) return true

    val prev = list[index - 1]
    if (item.dateStr != prev.dateStr) return true
    if (item.dateStr == prev.dateStr) return false

    return false
}