package com.lee.playcompose.square.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.CreateShareSuccessEvent
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ActionTextItem
import com.lee.playcompose.common.ui.widget.*
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.MyShareViewModel
import com.lee.playcompose.square.viewmodel.MyShareViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description
 */
@Composable
fun MySharePage(navController: NavController, viewModel: MyShareViewModel = viewModel()) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val viewState = viewModel.viewStates
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val slidingPaneState by rememberSlidingPaneState()

    // 监听创建分享成功事件刷新列表
    LaunchedEffect(Unit) {
        ChannelBus.bindChannel<CreateShareSuccessEvent>(lifecycle)?.receiveAsFlow()?.collect {
            delay(500)
            contentList.refresh()
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
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
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

