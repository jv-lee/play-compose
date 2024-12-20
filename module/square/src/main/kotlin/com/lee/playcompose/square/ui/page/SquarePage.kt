package com.lee.playcompose.square.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.ktx.LocalActivity
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.NavigationSelectEvent
import com.lee.playcompose.common.ktx.transformDetails
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.RouteBackHandler
import com.lee.playcompose.common.ui.widget.header.AppHeaderContainer
import com.lee.playcompose.common.ui.widget.header.AppTextActionBar
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.SquareViewModel
import com.lee.playcompose.square.viewmodel.SquareViewState
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * 首页第二个tab 广场页面
 * @author jv.lee
 * @date 2022/2/24
 */
@Composable
fun SquarePage(
    navController: NavController = LocalNavController.current,
    viewModel: SquareViewModel = viewModel()
) {
    val viewState = viewModel.viewStates
    val accountViewState = viewModel.accountService.getAccountViewStates(LocalActivity.current)

    LaunchedEffect(Unit) {
        // 监听channel全局事件NavigationSelectEvent:导航点击列表移动回顶部
        ChannelBus.getChannel<NavigationSelectEvent>()?.receiveAsFlow()?.collect { event ->
            if (event.route == RoutePage.Square.route) {
                viewState.listState.animateScrollToItem(0)
            }
        }
    }

    // double click close app.
    RouteBackHandler()

    Box(Modifier.background(ColorsTheme.colors.background)) {
        // content
        SquareContentList(viewState = viewState, onContentItemClick = {
            navController.navigateArgs(RoutePage.Details.route, it.transformDetails())
        })

        // header
        AppHeaderContainer {
            AppTextActionBar(
                title = stringResource(id = R.string.square_title),
                actionPainter = painterResource(id = R.drawable.vector_add),
                onActionClick = {
                    // 根据登陆状态跳转
                    val path = if (accountViewState.isLogin) RoutePage.Square.CreateShare.route
                    else RoutePage.Account.Login.route
                    navController.navigateArgs(path)
                }
            )
        }
    }
}

@Composable
private fun SquareContentList(
    viewState: SquareViewState,
    onContentItemClick: (Content) -> Unit
) {
    val contentList = viewState.savedPager.getLazyPagingItems()
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

        // build square content item
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
            ContentItem(item, onContentItemClick)
        }
    }
}
