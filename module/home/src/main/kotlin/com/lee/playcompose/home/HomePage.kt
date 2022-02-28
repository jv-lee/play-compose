package com.lee.playcompose.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.lee.playcompose.common.ui.widget.RefreshList

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun HomePage(navController: NavController) {
    val viewModel = viewModel<HomeViewModel>()
    val data = viewModel.projects.collectAsLazyPagingItems()
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)

    RefreshList(lazyPagingItems = data) {
        itemsIndexed(data) { _, item ->
            ItemView(item = item)
        }
    }
//    SwipeRefresh(state = refreshState, onRefresh = { data.refresh() }) {
//        LazyColumn(Modifier.fillMaxSize()) {
//            itemsIndexed(data) { _, item ->
//                ItemView(item = item)
//            }
//        }
//    }
}

@Composable
fun ItemView(item: String?) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(60.dp),
        Alignment.Center
    ) {
        Text(text = item ?: "")
    }
}
