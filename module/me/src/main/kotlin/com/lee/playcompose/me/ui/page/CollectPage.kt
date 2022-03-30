package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ActionTextItem
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.CollectViewModel
import com.lee.playcompose.me.viewmodel.CollectViewState
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs

/**
 * @author jv.lee
 * @date 2022/3/25
 * @description 个人收藏页
 */
@Composable
fun CollectPage(navController: NavController, viewModel: CollectViewModel = viewModel()) {
    val viewState = viewModel.viewStates

    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_collect),
        navigationClick = { navController.popBackStack() }) {
        CollectContent(viewState = viewState) {
            navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
        }
    }
}

@Composable
private fun CollectContent(viewState: CollectViewState, onItemClick: (Content) -> Unit) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
    ) {
        // build myShare content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            ActionTextItem(item = item, onItemClick = onItemClick)
        }
    }
}
