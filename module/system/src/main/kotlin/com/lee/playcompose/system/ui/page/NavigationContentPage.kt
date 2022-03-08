package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.flowlayout.FlowRow
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.NavigationItem
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.system.ui.theme.NavigationTabHeight
import com.lee.playcompose.system.ui.theme.SystemTabRadius
import com.lee.playcompose.system.viewmodel.NavigationContentViewModel
import com.lee.playcompose.system.viewmodel.NavigationContentViewState

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description
 */
@Composable
fun NavigationContentPage(
    navController: NavController,
    viewModel: NavigationContentViewModel = viewModel()
) {
    NavigationContent(viewState = viewModel.viewStates, itemClick = {
        toast(it.title)
    })
}

@Composable
private fun NavigationContent(
    viewState: NavigationContentViewState,
    itemClick: (Content) -> Unit
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()
    val tabState = if (contentList.itemCount > 0) viewState.tabState else LazyListState()

    Row(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(0.32f), tabState, content = {
            item { HeaderSpacer() }

            itemsIndexed(contentList) { _, item ->
                item ?: return@itemsIndexed
                NavigationTabItem(item = item, tabClick = {
                    toast(it.name)
                })
            }
        })

        LazyColumn(modifier = Modifier.weight(0.68f), listState, content = {
            item { HeaderSpacer() }

            itemsIndexed(contentList) { _, item ->
                item ?: return@itemsIndexed
                NavigationContentItem(item = item, itemClick = itemClick)
            }
        })
    }
}

@Composable
private fun NavigationTabItem(item: NavigationItem, tabClick: (NavigationItem) -> Unit) {
    val isSelect = false
    val textColor = if (isSelect) AppTheme.colors.onFocus else AppTheme.colors.primary
    val modifier = Modifier.background(
        color = if (isSelect) AppTheme.colors.focus else Color.Transparent,
        shape = RoundedCornerShape(SystemTabRadius)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(NavigationTabHeight)
            .wrapContentSize(align = Alignment.Center)
    ) {
        Box(modifier = modifier.clickable { tabClick(item) }) {
            Text(
                text = item.name,
                fontSize = FontSizeMedium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(OffsetMedium),
            )
        }
    }
}

@Composable
private fun NavigationContentItem(item: NavigationItem, itemClick: (Content) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Column {
            Text(
                text = item.name,
                color = AppTheme.colors.accent,
                fontSize = FontSizeMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(OffsetMedium))
            NavigationContentFlowList(item = item, itemClick = itemClick)
        }
    }
}

@Composable
private fun NavigationContentFlowList(item: NavigationItem, itemClick: (Content) -> Unit) {
    FlowRow {
        item.articles.forEach {
            Box(
                modifier = Modifier
                    .padding(OffsetSmall)
                    .background(
                        AppTheme.colors.item,
                        shape = RoundedCornerShape(OffsetRadiusSmall)
                    )
                    .clickable { itemClick(it) }
            ) {
                Text(
                    text = it.title,
                    color = AppTheme.colors.primary,
                    fontSize = FontSizeSmall,
                    modifier = Modifier.padding(OffsetMedium)
                )
            }
        }
    }
}
