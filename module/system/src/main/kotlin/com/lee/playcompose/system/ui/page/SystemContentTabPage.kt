package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/14
 * @description 体系页第一个tab 体系内容子tab页面
 */
@Composable
fun SystemContentTabPage(
    navController: NavController,
    parentTab: ParentTab? = null
) {
    parentTab ?: return
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var selectIndex by remember { mutableStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        selectIndex = pagerState.currentPage
    }

    AppBarViewContainer(
        title = parentTab.name,
        elevation = 0.dp,
        navigationClick = { navController.popBackStack() }) {
        Column {
            if (parentTab.children.isNotEmpty()) {
                IndicatorAdaptiveTabRow(
                    background = AppTheme.colors.item,
                    tabs = parentTab.children,
                    selectedTabIndex = selectIndex,
                    findTabText = { it.name },
                    onTabClick = { tabIndex ->
                        selectIndex = tabIndex
                        coroutine.launch { pagerState.animateScrollToPage(tabIndex) }
                    },
                )
                HorizontalPager(count = parentTab.children.size, state = pagerState) { page ->
                    val item = parentTab.children[page]
                    SystemContentListPage(navController = navController, tab = item)
                }
            }
        }
    }
}
