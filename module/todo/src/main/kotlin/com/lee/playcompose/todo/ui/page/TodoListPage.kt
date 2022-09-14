@file:OptIn(ExperimentalFoundationApi::class)

package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.base.extensions.forResult
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.callback.PageCallbackHandler
import com.lee.playcompose.common.ui.composable.HorizontallySpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.*
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.todo.R
import com.lee.playcompose.todo.ui.callback.TodoListCallback
import com.lee.playcompose.todo.ui.theme.*
import com.lee.playcompose.todo.viewmodel.TodoListViewAction
import com.lee.playcompose.todo.viewmodel.TodoListViewEvent
import com.lee.playcompose.todo.viewmodel.TodoListViewModel
import com.lee.playcompose.todo.viewmodel.TodoListViewState
import com.lee.playcompose.common.R as CR

/** 创建todo页面todo创建/修改状态后页面回传key（通知todoList页面数据变更进行刷新）*/
const val REQUEST_KEY_REFRESH = "requestKey:refresh"

/** 待完成TODO列表状态值 */
const val STATUS_UPCOMING = 0

/** 已完成TODO列表状态值 */
const val STATUS_COMPLETE = 1

/**
 * todo列表数据页 (待完成/已完成)
 * @param type: 0默认 1工作 2生活 3娱乐
 * @param status: 0待完成 1已完成
 * @author jv.lee
 * @date 2022/4/6
 */
@Composable
fun TodoListPage(
    navController: NavController = LocalNavController.current,
    type: Int,
    status: Int,
    handler: PageCallbackHandler<TodoListCallback>,
    viewModel: TodoListViewModel = viewModel(
        key = type.toString() + status.toString(),
        factory = TodoListViewModel.CreateFactory(type, status)
    )
) {
    val viewState = viewModel.viewStates
    val contentList = viewState.savedPager.getLazyPagingItems()
    val slidingPaneState by rememberSlidingPaneState()

    // 监听创建修改成功回调
    navController.forResult<Int>(key = REQUEST_KEY_REFRESH, 200) {
        it?.takeIf { it == status }?.run { contentList.refresh() }
    }

    // 监听多页面状态修改移除刷新联动回调
    handler.addCallback(status.toString(), object : TodoListCallback {
        override fun refresh() {
            contentList.refresh()
        }
    })

    // 监听页面type变化重新加载数据
    LaunchedEffect(type) {
        contentList.refresh()
    }

    // 监听viewModel单向事件
    LaunchedEffect(type, status) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is TodoListViewEvent.RefreshTodoData -> {
                    handler.notifyAt(event.statusKey) { it.refresh() }
                }
                is TodoListViewEvent.RequestFailed -> {
                    toast(event.message)
                }
                is TodoListViewEvent.ResetSlidingState -> {
                    slidingPaneState.closeAction()
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

@Composable
private fun TodoListContent(
    viewState: TodoListViewState,
    slidingPaneState: SlidingPaneState,
    onContentItemClick: (TodoData) -> Unit,
    onDelete: (TodoData) -> Unit,
    onUpdate: (TodoData) -> Unit,
) {
    val contentList = viewState.savedPager.getLazyPagingItems()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.item)
                .clickable { onContentItemClick(todoData) }
        ) {
            Text(
                text = todoData.title,
                fontSize = FontSizeMedium,
                color = AppTheme.colors.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = OffsetLarge, top = OffsetMedium, end = OffsetLarge)
            )
            Text(
                text = todoData.content,
                fontSize = FontSizeMedium,
                color = AppTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = OffsetLarge, bottom = OffsetMedium, end = OffsetLarge)
            )
        }

        // 重要标签构建角标
        if (todoData.priority == TodoData.PRIORITY_HIGH) ItemSubscript()

        // item line
        Box(modifier = Modifier.wrapContentHeight(align = Alignment.Bottom)) {
            HorizontallySpacer(color = AppTheme.colors.background)
        }
    }
}

@Composable
private fun ItemSubscript() {
    Canvas(modifier = Modifier.size(25.dp)) {
        val path = Path().apply {
            lineTo(0f, size.height)
            lineTo(size.width, 0f)
            lineTo(0f, 0f)
        }
        drawPath(path = path, color = Color.Red, style = Fill)
    }

    Text(
        text = stringResource(id = R.string.todo_item_level_height),
        fontSize = 8.sp,
        color = Color.White,
        modifier = Modifier
            .rotate(-45f)
            .offset(x = 3.dp, y = 2.dp)
            .size(width = 25.dp, height = 25.dp)
    )
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