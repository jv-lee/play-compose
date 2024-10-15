package com.lee.playcompose.common.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
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
    // loadPage Load.
    if ((lazyPagingItems.loadState.refresh is LoadState.Loading ||
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

    var isRefreshingState by remember { mutableStateOf(isRefreshing) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshingState,
        onRefresh = {
            onRefresh()
            lazyPagingItems.refresh()
        })

    isRefreshingState =
        ((lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing) && swipeEnable

    Box(modifier = Modifier.pullRefresh(state = pullRefreshState)) {

        // build list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            // item content
            itemContent()

            // item state
            if (!isRefreshingState) {
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

        // refresh indicator
        PullRefreshIndicator(
            refreshing = isRefreshingState,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(indicatorPadding)

        )
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