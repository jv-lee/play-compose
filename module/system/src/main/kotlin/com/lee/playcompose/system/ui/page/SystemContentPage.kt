package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.tools.WeakDataHolder
import com.lee.playcompose.common.entity.NavigationSelectEvent
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.extensions.formHtmlLabels
import com.lee.playcompose.common.ui.composable.CardItemContainer
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.RouteParamsKey
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.system.R
import com.lee.playcompose.system.viewmodel.SystemContentViewModel
import com.lee.playcompose.system.viewmodel.SystemContentViewState
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 体系页第一个tab 体系内容
 */
@Composable
fun SystemContentPage(
    navController: NavController,
    viewModel: SystemContentViewModel = viewModel()
) {
    val viewState = viewModel.viewStates

    LaunchedEffect(Unit) {
        // 监听channel全局事件NavigationSelectEvent:导航点击列表移动回顶部
        ChannelBus.getChannel<NavigationSelectEvent>()?.receiveAsFlow()?.collect { event ->
            if (event.route == RoutePage.System.route) {
                viewState.listState.animateScrollToItem(0)
            }
        }
    }

    SystemContentList(viewState = viewState, onItemClick = {
        // tabData跳转数据暂存 to SystemContentTabPage
        WeakDataHolder.instance.saveData(RouteParamsKey.tabDataKey, it)
        navController.navigateArgs(RoutePage.System.SystemContentTab.route)
    })
}

@Composable
private fun SystemContentList(
    viewState: SystemContentViewState,
    onItemClick: (ParentTab) -> Unit
) {
    val contentList = viewState.savedPager.getLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
        swipeEnable = false,
    ) {
        // header spacer
        item { HeaderSpacer() }

        // build system content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            SystemContentItem(item = item, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun SystemContentItem(item: ParentTab, onItemClick: (ParentTab) -> Unit) {
    CardItemContainer(onClick = { onItemClick(item) }) {
        Column {
            Text(
                text = item.name,
                color = AppTheme.colors.accent,
                fontSize = FontSizeMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OffsetMedium + OffsetMedium + 1.dp)
                    .padding(top = OffsetMedium, bottom = OffsetMedium)
                    .background(AppTheme.colors.background)
            )
            Text(
                text = item.formHtmlLabels(),
                color = AppTheme.colors.primary,
                fontSize = FontSizeSmall,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OffsetMedium + OffsetMedium + 1.dp)
                    .padding(top = OffsetMedium, bottom = OffsetMedium)
                    .background(AppTheme.colors.background)
            )
            Text(
                text = stringResource(id = R.string.item_more),
                color = AppTheme.colors.focus,
                fontSize = FontSizeSmallX,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.CenterEnd)
            )
        }
    }
}

