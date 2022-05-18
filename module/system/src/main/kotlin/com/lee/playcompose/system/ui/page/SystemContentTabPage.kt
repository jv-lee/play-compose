package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import com.lee.playcompose.system.viewmodel.SystemContentTabViewAction
import com.lee.playcompose.system.viewmodel.SystemContentTabViewModel
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/14
 * @description 体系页第一个tab 体系内容子tab页面
 */
@Composable
fun SystemContentTabPage(
    navController: NavController,
    parentTab: ParentTab? = null,
    viewModel: SystemContentTabViewModel = viewModel()
) {
    parentTab ?: return
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val selectIndex = viewModel.viewStates.selectedIndex

    LaunchedEffect(pagerState.currentPage) {
        viewModel.dispatch(SystemContentTabViewAction.SelectedTabIndex(pagerState.currentPage))
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
                        viewModel.dispatch(SystemContentTabViewAction.SelectedTabIndex(tabIndex))
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
