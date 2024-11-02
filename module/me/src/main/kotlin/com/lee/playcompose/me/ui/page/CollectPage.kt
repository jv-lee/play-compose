package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.toast
import com.lee.playcompose.common.ktx.transformDetails
import com.lee.playcompose.common.ui.composable.ActionTextItem
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.SlidingPaneState
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.rememberSlidingPaneState
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.CollectViewEvent
import com.lee.playcompose.me.viewmodel.CollectViewIntent
import com.lee.playcompose.me.viewmodel.CollectViewModel
import com.lee.playcompose.me.viewmodel.CollectViewState
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs

/**
 * 个人收藏页
 * @author jv.lee
 * @date 2022/3/25
 */
@Composable
fun CollectPage(
    navController: NavController = LocalNavController.current,
    viewModel: CollectViewModel = viewModel()
) {
    val viewState = viewModel.viewStates()
    val slidingPaneState by rememberSlidingPaneState()

    // 监听取消收藏事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is CollectViewEvent.UnCollectEvent -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_collect),
        navigationClick = { navController.popBackStack() },
        content = {
            CollectContent(
                viewState = viewState,
                slidingPaneState = slidingPaneState,
                onItemClick = {
                    navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
                },
                onItemDelete = {
                    viewModel.dispatch(CollectViewIntent.RequestUnCollect(it))
                }
            )
        }
    )
}

@Composable
private fun CollectContent(
    viewState: CollectViewState,
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
        // build collect content item
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