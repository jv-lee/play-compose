package com.lee.playcompose.common.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.lee.playcompose.base.extensions.delayState
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ui.composable.FooterSpacer
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.ListStateItemHeight

/**
 * 刷新列表组件
 * @param lazyPagingItems paging3compose列表数据
 * @param isRefreshing 是否处于刷新状态
 * @param swipeEnable 是否支持下拉刷新
 * @param onRefresh 刷新回调执行函数
 * @param indicatorPadding
 * @param listState 数据列表状态
 * @param itemContent 构建列表composable LazyListScope作用域
 * @author jv.lee
 * @date 2022/2/28
 */
@Composable
fun <T : Any> RefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    isRefreshing: Boolean = false,
    swipeEnable: Boolean = true,
    navigationPadding: Boolean = false,
    onRefresh: (() -> Unit) = {},
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState = rememberLazyListState(),
    itemContent: LazyListScope.() -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    // loadPage Load.
    if ((
        lazyPagingItems.loadState.refresh is LoadState.Loading ||
            lazyPagingItems.loadState.refresh is LoadState.NotLoading
        ) &&
        !lazyPagingItems.loadState.append.endOfPaginationReached &&
        lazyPagingItems.itemCount == 0
    ) {
        PageLoading()
        return
    }

    // loadPage Empty.
    if (lazyPagingItems.loadState.append is LoadState.NotLoading &&
        lazyPagingItems.loadState.append.endOfPaginationReached &&
        lazyPagingItems.itemCount == 0
    ) {
        PageEmpty()
        return
    }

    // loadPage Error.
    val error = lazyPagingItems.loadState.refresh is LoadState.Error
    if (error) {
        PageError { lazyPagingItems.retry() }
        return
    }

    SwipeRefresh(
        state = swipeRefreshState,
        swipeEnabled = swipeEnable,
        indicatorPadding = indicatorPadding,
        onRefresh = {
            onRefresh()
            lazyPagingItems.refresh()
        }
    ) {
        swipeRefreshState.isRefreshing =
            ((lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing) &&
            swipeEnable

        // build list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            // item content
            itemContent()

            // item state
            if (!swipeRefreshState.isRefreshing) {
                item {
                    lazyPagingItems.apply {
                        when (loadState.append) {
                            is LoadState.Loading -> ItemLoading()
                            is LoadState.Error -> ItemError { retry() }
                            is LoadState.NotLoading -> {
                                if (loadState.append.endOfPaginationReached) {
                                    ItemNoMore()
                                } else {
                                    ItemSpacer()
                                }
                            }
                        }
                    }
                }
                if (navigationPadding) {
                    item { FooterSpacer() }
                }
            }
        }
    }
}

@Composable
private fun PageError(retry: () -> Unit = { }) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        Column(modifier = Modifier.clickable { retry() }) {
            Image(
                painter = painterResource(id = R.drawable.vector_failed),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(id = R.string.page_load_error),
                color = ColorsTheme.colors.accent,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PageEmpty() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.vector_empty),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(id = R.string.page_load_empty),
                color = ColorsTheme.colors.accent,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PageLoading() {
    val visible = delayState(default = false, update = true)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        AnimatedVisibility(visible = visible.value) {
            CircularProgressIndicator(
                color = ColorsTheme.colors.accent,
                modifier = Modifier.height(50.dp)
            )
        }
    }
}

@Composable
private fun ItemError(retry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ListStateItemHeight)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = stringResource(id = R.string.item_load_error),
            color = ColorsTheme.colors.accent,
            modifier = Modifier.clickable {
                retry()
            }
        )
    }
}

@Composable
private fun ItemNoMore() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ListStateItemHeight)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = stringResource(id = R.string.item_load_end), color = ColorsTheme.colors.accent)
    }
}

@Composable
private fun ItemLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ListStateItemHeight)
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = ColorsTheme.colors.accent,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = stringResource(id = R.string.item_load_more), color = ColorsTheme.colors.accent)
    }
}

@Composable
private fun ItemSpacer() {
    Spacer(modifier = Modifier.height(ListStateItemHeight))
}