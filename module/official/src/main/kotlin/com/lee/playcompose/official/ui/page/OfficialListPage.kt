package com.lee.playcompose.official.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.Tab
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.official.viewmodel.OfficialListViewModel
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.router.navigateArgs

/**
 * @author jv.lee
 * @date 2022/3/11
 * @description
 */
@Composable
fun OfficialListPage(
    navController: NavController,
    item: Tab,
    viewModel: OfficialListViewModel = viewModel(
        key = item.id.toString(),
        factory = OfficialListViewModel.CreateFactory(item.id)
    )
) {
    val viewState = viewModel.viewStates
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
    ) {
        // build home content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            ContentItem(item, onItemClick = {
                navController.navigateArgs(PageRoute.Details.route, it.transformDetails())
            })
        }
    }
}