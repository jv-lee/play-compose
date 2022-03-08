package com.lee.playcompose.home.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.px2dp
import com.lee.playcompose.common.entity.Banner
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.AppBarContainer
import com.lee.playcompose.common.ui.widget.AppGradientTextBar
import com.lee.playcompose.common.ui.widget.BannerView
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.home.R
import com.lee.playcompose.home.model.entity.HomeCategory
import com.lee.playcompose.home.viewmodel.HomeViewAction
import com.lee.playcompose.home.viewmodel.HomeViewModel
import com.lee.playcompose.home.viewmodel.HomeViewState
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.router.navigationArgs
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 首页 homeTab页
 */
@ExperimentalCoilApi
@Composable
fun HomePage(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = viewModel()
) {
    val viewState = viewModel.viewStates

    Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
        // content
        HomeContentList(
            viewState = viewState,
            onRefresh = { viewModel.dispatch(HomeViewAction.RequestData) },
            onBannerItemClick = { toast(it.title) },
            onCategoryItemClick = { toast(it.name) },
            onContentItemClick = {
                navController.navigationArgs(PageRoute.Details.route, DetailsData(it.title, it.link))
            }
        )

        // header
        AppBarContainer {
            AppGradientTextBar(
                title = stringResource(id = R.string.home_header_text),
                navigationPainter = painterResource(id = CR.drawable.vector_search),
                onNavigationClick = { toast("search click") }
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun HomeContentList(
    viewState: HomeViewState,
    onRefresh: () -> Unit,
    onBannerItemClick: (Banner) -> Unit,
    onCategoryItemClick: (HomeCategory) -> Unit,
    onContentItemClick: (Content) -> Unit,
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val bannerList = viewState.banners
    val categoryList = viewState.category
    val isRefreshing = viewState.isRefreshing
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
        listState = listState,
        indicatorPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.statusBars,
            applyTop = true,
            additionalTop = ToolBarHeight
        )
    ) {

        // header spacer
        item {
            Spacer(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(ToolBarHeight)
            )
        }

        // build home banner item
        if (bannerList.isNotEmpty()) {
            item {
                BannerView(
                    data = bannerList,
                    onItemClick = onBannerItemClick,
                    findPath = { item -> item.imagePath },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        // build home category item
        if (categoryList.isNotEmpty()) {
            item {
                HomeCategoryItem(categoryList, onCategoryItemClick)
            }
        }

        // build home content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            ContentItem(item, onContentItemClick)
        }
    }
}

@Composable
private fun HomeCategoryItem(
    categoryList: List<HomeCategory>,
    onItemClick: (HomeCategory) -> Unit
) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(categoryList) { item ->
            HomeCategoryChildItem(category = item) {
                onItemClick(it)
            }
        }
    }
}

@Composable
private fun HomeCategoryChildItem(
    category: HomeCategory,
    onItemClick: (HomeCategory) -> Unit
) {
    val screenWidth = app.resources.displayMetrics.widthPixels
    val viewWidth = app.px2dp(screenWidth / 2)
    Box(
        modifier = Modifier
            .width(viewWidth.dp)
            .padding(OffsetMedium)
    ) {
        Card(backgroundColor = AppTheme.colors.item) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable { onItemClick(category) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(OffsetLarge)
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = category.iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(OffsetMedium))
                    Text(text = category.name, color = AppTheme.colors.accent)
                }
            }
        }
    }
}
