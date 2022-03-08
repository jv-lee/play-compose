package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.system.viewmodel.SystemContentViewModel
import com.lee.playcompose.system.viewmodel.SystemContentViewState

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description
 */
@Composable
fun SystemContentPage(
    navController: NavController,
    viewModel: SystemContentViewModel = viewModel()
) {
    SystemContentList(viewState = viewModel.viewStates, onContentItemClick = {

    })
}

@Composable
private fun SystemContentList(
    viewState: SystemContentViewState,
    onContentItemClick: (ParentTab) -> Unit
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
        swipeEnable = false,
    ) {
        // header spacer
        item { HeaderSpacer() }

        // build system content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            SystemContentItem(item = item, onContentItemClick = onContentItemClick)
        }
    }
}

@Composable
private fun SystemContentItem(item: ParentTab, onContentItemClick: (ParentTab) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = item.name)
    }
}