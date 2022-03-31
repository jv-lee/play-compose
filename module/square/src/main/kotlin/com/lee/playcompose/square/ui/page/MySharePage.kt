package com.lee.playcompose.square.ui.page

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
import com.lee.playcompose.common.ui.widget.ActionMode
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.MyShareViewModel
import com.lee.playcompose.square.viewmodel.MyShareViewState

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description
 */
@Composable
fun MySharePage(navController: NavController, viewModel: MyShareViewModel = viewModel()) {
    val viewState = viewModel.viewStates

    AppBarViewContainer(
        title = stringResource(id = R.string.square_my_share_title),
        actionIcon = R.drawable.vector_add,
        actionMode = ActionMode.Button,
        navigationClick = { navController.popBackStack() },
        actionClick = { navController.navigate(RoutePage.Square.CreateShare.route) }) {
        MyShareContent(viewState = viewState) {
            navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
        }
    }
}

@Composable
private fun MyShareContent(viewState: MyShareViewState, onItemClick: (Content) -> Unit) {
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

