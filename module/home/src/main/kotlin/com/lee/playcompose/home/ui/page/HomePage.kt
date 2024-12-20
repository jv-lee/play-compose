package com.lee.playcompose.home.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.base.ktx.ScreenSizeChange
import com.lee.playcompose.common.entity.Banner
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.ContentVisibleEvent
import com.lee.playcompose.common.entity.NavigationSelectEvent
import com.lee.playcompose.common.ktx.transformDetails
import com.lee.playcompose.common.ui.composable.CardItemContainer
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.BannerView
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.RouteBackHandler
import com.lee.playcompose.common.ui.widget.header.AppHeaderContainer
import com.lee.playcompose.common.ui.widget.header.AppTextActionBar
import com.lee.playcompose.home.R
import com.lee.playcompose.home.model.entity.HomeCategory
import com.lee.playcompose.home.viewmodel.HomeViewIntent
import com.lee.playcompose.home.viewmodel.HomeViewModel
import com.lee.playcompose.home.viewmodel.HomeViewState
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import kotlinx.coroutines.flow.receiveAsFlow
import com.lee.playcompose.common.R as CR

/**
 * 首页第一个tab 主页
 * @author jv.lee
 * @date 2022/2/24
 */
@Composable
fun HomePage(
    navController: NavController = LocalNavController.current,
    viewModel: HomeViewModel = viewModel()
) {
    val viewState = viewModel.viewStates

    LaunchedEffect(Unit) {
        // 监听channel全局事件NavigationSelectEvent:导航点击列表移动回顶部
        ChannelBus.getChannel<NavigationSelectEvent>()?.receiveAsFlow()?.collect { event ->
            if (event.route == RoutePage.Home.route) {
                viewState.listState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(Unit) {
        // 监听内容显示事件
        ChannelBus.getChannel<ContentVisibleEvent>()?.receiveAsFlow()?.collect {
            viewModel.dispatch(HomeViewIntent.RequestLoopBanner)
        }
    }

    // double click close app.
    RouteBackHandler()

    Box(Modifier.background(ColorsTheme.colors.background)) {
        // content
        HomeContentList(
            viewState = viewState,
            onBannerItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            },
            onCategoryItemClick = {
                navController.navigateArgs(it.route)
            },
            onContentItemClick = {
                navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
            }
        )

        // header
        AppHeaderContainer {
            AppTextActionBar(
                title = stringResource(id = R.string.home_header_text),
                actionPainter = painterResource(id = CR.drawable.vector_search),
                onActionClick = {
                    navController.navigateArgs(RoutePage.Search.route)
                }
            )
        }
    }
}

@Composable
private fun HomeContentList(
    viewState: HomeViewState,
    onBannerItemClick: (Banner) -> Unit,
    onCategoryItemClick: (HomeCategory) -> Unit,
    onContentItemClick: (Content) -> Unit
) {
    val contentList = viewState.savedPager.getLazyPagingItems()
    val bannerList = viewState.banners
    val categoryList = viewState.category
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
        navigationPadding = true,
        indicatorPadding = WindowInsets(
            top = ToolBarHeight + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        ).asPaddingValues()
    ) {
        // header spacer
        item { HeaderSpacer() }

        // build home banner item
        if (bannerList.isNotEmpty()) {
            item {
                BannerView(
                    data = bannerList,
                    onItemClick = onBannerItemClick,
                    findPath = { item -> item.imagePath },
                    loopEnable = viewState.isLoop,
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
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
            ContentItem(item, onContentItemClick)
        }
    }
}

@Composable
private fun HomeCategoryItem(
    categoryList: List<HomeCategory>,
    onItemClick: (HomeCategory) -> Unit
) {
    ScreenSizeChange { width, _ ->
        // 屏幕宽度的一半
        val viewWidth = (width / 2).dp
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(categoryList) { item ->
                HomeCategoryChildItem(viewWidth, category = item) {
                    onItemClick(it)
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryChildItem(
    viewWidth: Dp,
    category: HomeCategory,
    onItemClick: (HomeCategory) -> Unit
) {
    CardItemContainer(modifier = Modifier.width(viewWidth), onClick = { onItemClick(category) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = category.iconResId),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(OffsetMedium))
            Text(
                text = stringResource(id = category.nameResId),
                color = ColorsTheme.colors.accent
            )
        }
    }
}
