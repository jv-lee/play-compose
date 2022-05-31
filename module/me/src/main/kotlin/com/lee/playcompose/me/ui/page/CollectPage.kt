package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ActionTextItem
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.SlidingPaneState
import com.lee.playcompose.common.ui.widget.rememberSlidingPaneState
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.CollectViewAction
import com.lee.playcompose.me.viewmodel.CollectViewEvent
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
fun CollectPage(navController: NavController, viewModel: CollectViewModel = viewModel()) {
    val viewState = viewModel.viewStates
    val slidingPaneState by rememberSlidingPaneState()

    // 监听取消收藏事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is CollectViewEvent.UnCollectEvent -> {
                    toast(event.message)
                    slidingPaneState.closeAction()
                }
            }
        }
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_collect),
        navigationClick = { navController.popBackStack() }) {
        CollectContent(
            viewState = viewState,
            slidingPaneState = slidingPaneState,
            onItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            },
            onItemDelete = {
                viewModel.dispatch(CollectViewAction.RequestUnCollect(it))
            })
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CollectContent(
    viewState: CollectViewState,
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