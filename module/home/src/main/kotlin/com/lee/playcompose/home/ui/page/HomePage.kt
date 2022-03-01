package com.lee.playcompose.home.ui.page

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
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.home.viewmodel.HomeViewAction
import com.lee.playcompose.home.viewmodel.HomeViewModel

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun HomePage(navController: NavController) {
    val viewModel = viewModel<HomeViewModel>()
    val viewState = viewModel.viewStates

    val homeContent = viewState.pagingData.collectAsLazyPagingItems()
    val homeBanner = viewState.banners
    val homeCategory = viewState.category
    val isRefreshing = viewState.isRefreshing

    RefreshList(lazyPagingItems = homeContent, isRefreshing = isRefreshing, onRefresh = {
        viewModel.dispatch(HomeViewAction.RequestData)
    }) {
        // build home banner item
        if (homeBanner.isNotEmpty()) {

        }

        // build home category item
        if (homeCategory.isNotEmpty()) {

        }

        // build home content item
        itemsIndexed(homeContent) { _, item ->
            ItemView(item = item)
        }
    }
}

@Composable
private fun ItemView(item: Content?) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(60.dp),
        Alignment.Center
    ) {
        Text(text = item?.title ?: "")
    }
}
