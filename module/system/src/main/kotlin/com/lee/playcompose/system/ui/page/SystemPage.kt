package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.common.entity.NavigationSelectEvent
import com.lee.playcompose.common.ui.callback.rememberPageCallbackHandler
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.header.AppHeaderContainer
import com.lee.playcompose.common.ui.widget.RouteBackHandler
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.system.R
import com.lee.playcompose.system.ui.callback.SystemCallback
import com.lee.playcompose.system.ui.theme.SystemTabHeight
import com.lee.playcompose.system.ui.theme.SystemTabRadius
import com.lee.playcompose.system.ui.theme.SystemTabWidth
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 首页第三个tab 体系页
 * @author jv.lee
 * @date 2022/2/24
 */
@Composable
fun SystemPage() {
    val pagerState = rememberPagerState()
    val handler by rememberPageCallbackHandler<SystemCallback>(LocalLifecycleOwner.current)

    LaunchedEffect(Unit) {
        // 监听channel全局事件NavigationSelectEvent:导航点击列表移动回顶部
        ChannelBus.getChannel<NavigationSelectEvent>()?.receiveAsFlow()?.collect { event ->
            if (event.route == RoutePage.System.route) {
                val key = if (pagerState.currentPage == 0) {
                    systemCallbackKey
                } else navigationCallbackKey
                handler.notifyAt(key) { it.tabChange() }
            }
        }
    }

    // double click close app.
    RouteBackHandler()

    Box(Modifier.background(ColorsTheme.colors.background)) {
        // pageContent
        HorizontalPager(count = 2, state = pagerState) { page ->
            when (page) {
                0 -> SystemContentPage(handler)
                1 -> NavigationContentPage(handler)
            }
        }

        // header
        AppHeaderContainer {
            TabContainer(pagerState = pagerState)
        }
    }
}

@Composable
private fun TabContainer(pagerState: PagerState) {
    val coroutine = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ToolBarHeight)
            .wrapContentSize(Alignment.Center)
    ) {
        TabButton(stringResource(id = R.string.tab_system), pagerState.currentPage == 0) {
            coroutine.launch { pagerState.scrollToPage(0) }
        }
        Spacer(modifier = Modifier.width(OffsetLarge))
        TabButton(stringResource(id = R.string.tab_navigation), pagerState.currentPage == 1) {
            coroutine.launch { pagerState.scrollToPage(1) }
        }
    }
}

@Composable
private fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) ColorsTheme.colors.focus else ColorsTheme.colors.onFocus
    val textColor = if (isSelected) ColorsTheme.colors.onFocus else ColorsTheme.colors.focus
    Box(
        modifier = Modifier
            .size(width = SystemTabWidth, height = SystemTabHeight)
            .background(color = color, shape = RoundedCornerShape(SystemTabRadius))
            .wrapContentSize(Alignment.Center)
            .clickable { onClick() }
    ) {
        Text(text = text, fontSize = FontSizeTheme.sizes.medium, color = textColor)
    }
}
