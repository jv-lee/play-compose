package com.lee.playcompose.system.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.flowlayout.FlowRow
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.NavigationItem
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.callback.PageCallbackHandler
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.UiStatusListPage
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.system.ui.callback.SystemCallback
import com.lee.playcompose.system.ui.theme.NavigationTabHeight
import com.lee.playcompose.system.ui.theme.SystemTabRadius
import com.lee.playcompose.system.viewmodel.NavigationContentViewModel
import com.lee.playcompose.system.viewmodel.NavigationContentViewState
import kotlinx.coroutines.launch

const val navigationCallbackKey = "navigationCallbackKey"

/**
 * 体系页第二个tab 导航内容
 * @author jv.lee
 * @date 2022/3/8
 */
@Composable
fun NavigationContentPage(
    handler: PageCallbackHandler<SystemCallback>,
    navController: NavController = LocalNavController.current,
    viewModel: NavigationContentViewModel = viewModel()
) {
    val coroutine = rememberCoroutineScope()
    val viewState = viewModel.viewStates

    handler.addCallback(
        navigationCallbackKey,
        object : SystemCallback {
            override fun tabChange() {
                coroutine.launch { viewState.listState.animateScrollToItem(0) }
            }
        }
    )

    NavigationContent(viewState = viewState, itemClick = {
        navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
    })
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
private fun NavigationContent(
    viewState: NavigationContentViewState,
    itemClick: (Content) -> Unit
) {
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val toolbarOffset = (statusInsets.calculateTopPadding() + ToolBarHeight)

    val contentList = viewState.savedPager.getLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()
    val tabState = if (contentList.itemCount > 0) viewState.tabState else LazyListState()
    val currentIndex = remember { mutableStateOf(0) }
    val upsetIndex = remember { mutableStateOf(true) }
    val coroutine = rememberCoroutineScope()

    // 监听滚动状态联动
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (upsetIndex.value) {
            val index = listState.firstVisibleItemIndex
            tabState.scrollToItem(index)
            currentIndex.value = index
        } else {
            upsetIndex.value = true
        }
    }

    UiStatusListPage(loadState = contentList.loadState, retry = { contentList.retry() }) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(top = toolbarOffset, bottom = NavigationBarHeight)
        ) {
            LazyColumn(modifier = Modifier.weight(0.32f), tabState, content = {
                itemsIndexed(contentList) { index, item ->
                    item ?: return@itemsIndexed
                    NavigationTabItem(currentIndex.value == index, item = item, tabClick = {
                        currentIndex.value = index
                        upsetIndex.value = false
                        coroutine.launch { listState.scrollToItem(index) }
                    })
                }
            })

            LazyColumn(modifier = Modifier.weight(0.68f), listState, content = {
                itemsIndexed(contentList) { _, item ->
                    item ?: return@itemsIndexed
                    NavigationContentItem(item = item, itemClick = itemClick)
                }
            })
        }
    }
}

@Composable
private fun NavigationTabItem(
    isSelected: Boolean,
    item: NavigationItem,
    tabClick: (NavigationItem) -> Unit
) {
    val textColor = if (isSelected) ColorsTheme.colors.onFocus else ColorsTheme.colors.primary
    val tabColor = if (isSelected) ColorsTheme.colors.focus else Color.Transparent
    Card(
        elevation = 0.dp,
        backgroundColor = tabColor,
        shape = RoundedCornerShape(SystemTabRadius),
        modifier = Modifier
            .fillMaxWidth()
            .height(NavigationTabHeight)
            .wrapContentSize(align = Alignment.Center)
    ) {
        Text(
            text = item.name,
            fontSize = FontSizeTheme.sizes.medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable { tabClick(item) }
                .padding(OffsetMedium)
        )
    }
}

@Composable
private fun NavigationContentItem(item: NavigationItem, itemClick: (Content) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Text(
            text = item.name,
            color = ColorsTheme.colors.accent,
            fontSize = FontSizeTheme.sizes.medium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(OffsetMedium))
        NavigationContentFlowList(item = item, itemClick = itemClick)
    }
}

@Composable
private fun NavigationContentFlowList(item: NavigationItem, itemClick: (Content) -> Unit) {
    FlowRow {
        item.articles.forEach {
            Card(
                elevation = 0.dp,
                backgroundColor = ColorsTheme.colors.item,
                shape = RoundedCornerShape(OffsetRadiusSmall),
                modifier = Modifier.padding(OffsetSmall)
            ) {
                Text(
                    text = it.title,
                    color = ColorsTheme.colors.primary,
                    fontSize = FontSizeTheme.sizes.small,
                    modifier = Modifier
                        .clickable { itemClick(it) }
                        .padding(OffsetMedium)
                )
            }
        }
    }
}
