package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.system.viewmodel.SystemContentTabViewIntent
import com.lee.playcompose.system.viewmodel.SystemContentTabViewModel
import kotlinx.coroutines.launch

/**
 * 体系页第一个tab 体系内容子tab页面
 * @author jv.lee
 * @date 2022/3/14
 */
@Composable
fun SystemContentTabPage(
    parentTab: ParentTab? = null,
    navController: NavController = LocalNavController.current,
    viewModel: SystemContentTabViewModel = viewModel()
) {
    parentTab ?: return
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { parentTab.children.size })
    val selectIndex = viewModel.viewStates.selectedIndex

    LaunchedEffect(pagerState.currentPage) {
        viewModel.dispatch(SystemContentTabViewIntent.SelectedTabIndex(pagerState.currentPage))
    }

    AppBarViewContainer(
        title = parentTab.name,
        elevation = 0.dp,
        navigationClick = {
            navController.popBackStack()
        }
    ) {
        Column {
            if (parentTab.children.isNotEmpty()) {
                IndicatorAdaptiveTabRow(
                    background = ColorsTheme.colors.item,
                    tabs = parentTab.children,
                    selectedTabIndex = selectIndex,
                    findTabText = { it.name },
                    onTabClick = { tabIndex ->
                        viewModel.dispatch(SystemContentTabViewIntent.SelectedTabIndex(tabIndex))
                        coroutine.launch { pagerState.animateScrollToPage(tabIndex) }
                    }
                )
                HorizontalPager(state = pagerState) { page ->
                    val item = parentTab.children[page]
                    SystemContentListPage(navController = navController, tab = item)
                }
            }
        }
    }
}
