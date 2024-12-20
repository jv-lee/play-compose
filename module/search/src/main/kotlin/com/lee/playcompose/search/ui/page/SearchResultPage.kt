package com.lee.playcompose.search.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.transformDetails
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.composable.ContentPictureItem
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.search.viewmodel.SearchResultViewModel
import com.lee.playcompose.search.viewmodel.SearchResultViewState

/**
 * 搜索结果页
 * @author jv.lee
 * @date 2022/3/16
 */
@Composable
fun SearchResultPage(
    searchKey: String,
    navController: NavController = LocalNavController.current,
    viewModel: SearchResultViewModel = viewModel(
        factory = SearchResultViewModel.CreateFactory(searchKey)
    )
) {
    AppBarViewContainer(
        title = searchKey,
        backgroundColor = ColorsTheme.colors.item,
        navigationClick = { navController.popBackStack() },
        content = {
            SearchResultContent(viewModel.viewStates, onContentItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            })
        }
    )
}

@Composable
private fun SearchResultContent(
    viewState: SearchResultViewState,
    onContentItemClick: (Content) -> Unit
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState
    ) {
        // build searchResult content item
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
            if (item.envelopePic.isEmpty()) {
                ContentItem(item, onContentItemClick)
            } else {
                ContentPictureItem(item = item, onItemClick = onContentItemClick)
            }
        }
    }
}
