package com.lee.playcompose.system.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.NavigationItem
import com.lee.playcompose.common.extensions.transformDetails
import com.lee.playcompose.common.ui.callback.PageCallbackHandler
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.NavigationBarHeight
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetRadiusSmall
import com.lee.playcompose.common.ui.theme.OffsetSmall
import com.lee.playcompose.common.ui.theme.ToolBarHeight
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
    val currentIndex = remember { mutableIntStateOf(0) }
    val upsetIndex = remember { mutableStateOf(true) }
    val coroutine = rememberCoroutineScope()

    // 监听滚动状态联动
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (upsetIndex.value) {
            val index = listState.firstVisibleItemIndex
            tabState.scrollToItem(index)
            currentIndex.intValue = index
        } else {
            upsetIndex.value = true
        }
    }

    UiStatusListPage(
        loadState = contentList.loadState,
        retry = { contentList.retry() }) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(top = toolbarOffset, bottom = NavigationBarHeight)
        ) {
            LazyColumn(
                modifier = Modifier.weight(0.32f),
                state = tabState,
                content = {
                    items(contentList.itemCount) { index ->
                        val item = contentList[index] ?: return@items
                        NavigationTabItem(currentIndex.intValue == index, item = item, tabClick = {
                            currentIndex.intValue = index
                            upsetIndex.value = false
                            coroutine.launch { listState.scrollToItem(index) }
                        })
                    }
                })

            LazyColumn(
                modifier = Modifier.weight(0.68f),
                state = listState,
                content = {
                    items(contentList.itemCount) { index ->
                        val item = contentList[index] ?: return@items
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

@OptIn(ExperimentalLayoutApi::class)
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
