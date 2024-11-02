package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.lee.playcompose.common.entity.Tab
import com.lee.playcompose.common.ktx.transformDetails
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.system.viewmodel.SystemContentListViewModel

/**
 * 体系页第一个tab 体系内容子tabList页面
 * @author jv.lee
 * @date 2022/3/14
 */
@Composable
fun SystemContentListPage(
    navController: NavController,
    tab: Tab,
    viewModel: SystemContentListViewModel = viewModel(
        key = tab.id.toString(),
        factory = SystemContentListViewModel.CreateFactory(tab.id)
    )
) {
    val viewState = viewModel.viewStates
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState
    ) {
        // build system content list item
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
            ContentItem(item, onItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            })
        }
    }
}