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
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs

/**
 * 公众号列表页
 * @author jv.lee
 * @date 2022/3/11
 */
@Composable
fun OfficialListPage(
    navController: NavController,
    tab: Tab,
    viewModel: OfficialListViewModel = viewModel(
        key = tab.id.toString(),
        factory = OfficialListViewModel.CreateFactory(tab.id)
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
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            })
        }
    }
}