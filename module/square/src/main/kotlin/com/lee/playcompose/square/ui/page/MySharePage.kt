package com.lee.playcompose.square.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.base.extensions.forResult
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ActionTextItem
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.SlidingPaneState
import com.lee.playcompose.common.ui.widget.header.ActionMode
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.rememberSlidingPaneState
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.MyShareViewIntent
import com.lee.playcompose.square.viewmodel.MyShareViewEvent
import com.lee.playcompose.square.viewmodel.MyShareViewModel
import com.lee.playcompose.square.viewmodel.MyShareViewState

/** 创建分享内容成功后回调我的分享页面回传key （通知分享列表已变更进行数据刷新） */
const val REQUEST_KEY_SHARE_REFRESH = "requestKey:share_refresh"

/**
 * 我的分享列表页
 * @author jv.lee
 * @date 2022/3/16
 */
@Composable
fun MySharePage(
    navController: NavController = LocalNavController.current,
    viewModel: MyShareViewModel = viewModel()
) {
    val viewState = viewModel.viewStates()
    val contentList = viewState.savedPager?.getLazyPagingItems()
    val slidingPaneState by rememberSlidingPaneState()

    // 监听创建分享成功事件刷新列表
    navController.forResult<Int>(key = REQUEST_KEY_SHARE_REFRESH, delayTimeMillis = 500) {
        contentList?.refresh()
    }

    // 监听移除分享内容事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is MyShareViewEvent.DeleteShareEvent -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    AppBarViewContainer(
        title = stringResource(id = R.string.square_my_share_title),
        actionIcon = R.drawable.vector_add,
        actionMode = ActionMode.Button,
        navigationClick = { navController.popBackStack() },
        actionClick = {
            navController.navigateArgs(RoutePage.Square.CreateShare.route)
        }
    ) {
        MyShareContent(
            viewState = viewState,
            slidingPaneState = slidingPaneState,
            onItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            },
            onItemDelete = {
                viewModel.dispatch(MyShareViewIntent.RequestDeleteShare(it))
            }
        )
    }
}

@Composable
private fun MyShareContent(
    viewState: MyShareViewState,
    slidingPaneState: SlidingPaneState,
    onItemClick: (Content) -> Unit,
    onItemDelete: (Content) -> Unit
) {
    val contentList = viewState.savedPager?.getLazyPagingItems() ?: return
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState
    ) {
        // build myShare content item
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
            ActionTextItem(
                item = item,
                state = slidingPaneState,
                onItemClick = onItemClick,
                onItemDelete = onItemDelete
            )
        }
    }
}
