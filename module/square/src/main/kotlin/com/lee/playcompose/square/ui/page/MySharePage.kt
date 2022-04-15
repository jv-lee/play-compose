package com.lee.playcompose.square.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.base.extensions.forResult
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ActionTextItem
import com.lee.playcompose.common.ui.widget.*
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.square.Constants
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.MyShareViewAction
import com.lee.playcompose.square.viewmodel.MyShareViewEvent
import com.lee.playcompose.square.viewmodel.MyShareViewModel
import com.lee.playcompose.square.viewmodel.MyShareViewState
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description 我的分享列表页
 */
@Composable
fun MySharePage(navController: NavController, viewModel: MyShareViewModel = viewModel()) {
    val viewState = viewModel.viewStates
    val contentList = viewState.savedPager.getLazyPagingItems()
    val slidingPaneState by rememberSlidingPaneState()

    // 监听创建分享成功事件刷新列表
    navController.forResult<Int>(key = Constants.REQUEST_KEY_SHARE_REFRESH, delay = 500) {
        contentList.refresh()
    }

    // 监听移除分享内容事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is MyShareViewEvent.DeleteShareEvent -> {
                    toast(event.message)
                    slidingPaneState.closeAction()
                }
            }
        }
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.square_my_share_title),
        actionIcon = R.drawable.vector_add,
        actionMode = ActionMode.Button,
        navigationClick = { navController.popBackStack() },
        actionClick = { navController.navigate(RoutePage.Square.CreateShare.route) }) {
        MyShareContent(
            viewState = viewState,
            slidingPaneState = slidingPaneState,
            onItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            },
            onItemDelete = {
                viewModel.dispatch(MyShareViewAction.RequestDeleteShare(it))
            })
    }
}

@Composable
private fun MyShareContent(
    viewState: MyShareViewState,
    slidingPaneState: SlidingPaneState,
    onItemClick: (Content) -> Unit,
    onItemDelete: (Content) -> Unit
) {
    val contentList = viewState.savedPager.getLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
    ) {
        // build myShare content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            ActionTextItem(
                item = item,
                state = slidingPaneState,
                onItemClick = onItemClick,
                onItemDelete = onItemDelete,
            )
        }
    }
}

