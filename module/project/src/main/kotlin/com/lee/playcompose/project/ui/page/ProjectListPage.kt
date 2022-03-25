package com.lee.playcompose.project.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.Tab
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.composable.ContentPictureItem
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.project.viewmodel.ProjectListViewModel
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs

/**
 * @author jv.lee
 * @date 2022/3/11
 * @description
 */
@Composable
fun ProjectListPage(
    navController: NavController,
    tab: Tab,
    viewModel: ProjectListViewModel = viewModel(
        key = tab.id.toString(),
        factory = ProjectListViewModel.CreateFactory(tab.id)
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
            ContentPictureItem(item, onItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            })
        }
    }
}