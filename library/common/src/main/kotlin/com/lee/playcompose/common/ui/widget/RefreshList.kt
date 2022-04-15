package com.lee.playcompose.common.ui.widget

import android.annotation.SuppressLint
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
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.ListStateItemHeight

/**
 * @author jv.lee
 * @date 2022/2/28
 * @description 刷新列表组件
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun <T : Any> RefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    isRefreshing: Boolean = false,
    swipeEnable: Boolean = true,
    onRefresh: (() -> Unit) = {},
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState = rememberLazyListState(),
    itemContent: LazyListScope.() -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    // loadPage Load.
    if ((lazyPagingItems.loadState.refresh is LoadState.Loading
                || lazyPagingItems.loadState.refresh is LoadState.NotLoading)
        && !lazyPagingItems.loadState.append.endOfPaginationReached
        && lazyPagingItems.itemCount == 0
    ) {
        PageLoading()
        return
    }

    // loadPage Empty.
    if (lazyPagingItems.loadState.append is LoadState.NotLoading
        && lazyPagingItems.loadState.append.endOfPaginationReached
        && lazyPagingItems.itemCount == 0
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
        }) {
        swipeRefreshState.isRefreshing =
            ((lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing) && swipeEnable

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
                color = AppTheme.colors.accent,
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
                color = AppTheme.colors.accent,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PageLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        CircularProgressIndicator(color = AppTheme.colors.accent, modifier = Modifier.height(50.dp))
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
            color = AppTheme.colors.accent,
            modifier = Modifier.clickable {
                retry()
            })
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
        Text(text = stringResource(id = R.string.item_load_end), color = AppTheme.colors.accent)
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
            color = AppTheme.colors.accent,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = stringResource(id = R.string.item_load_more), color = AppTheme.colors.accent)
    }
}

@Composable
private fun ItemSpacer() {
    Spacer(modifier = Modifier.height(ListStateItemHeight))
}