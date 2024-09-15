package com.lee.playcompose.official.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import com.lee.playcompose.common.ui.widget.UiStatusPage
import com.lee.playcompose.official.R
import com.lee.playcompose.official.viewmodel.OfficialViewIntent
import com.lee.playcompose.official.viewmodel.OfficialViewModel
import kotlinx.coroutines.launch

/**
 * 公众号Tab页
 * @author jv.lee
 * @date 2022/3/11
 */
@Composable
fun OfficialPage(
    navController: NavController = LocalNavController.current,
    viewModel: OfficialViewModel = viewModel()
) {
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val selectIndex = viewModel.viewStates.selectedIndex
    val tabData = viewModel.viewStates.tab
    val uiStatus = viewModel.viewStates.uiStatus

    LaunchedEffect(pagerState.currentPage) {
        viewModel.dispatch(OfficialViewIntent.SelectedTabIndex(pagerState.currentPage))
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.official_title),
        elevation = 0.dp,
        navigationClick = { navController.popBackStack() }
    ) {
        UiStatusPage(
            status = uiStatus,
            retry = { viewModel.dispatch(OfficialViewIntent.RequestTabData) }
        ) {
            Column {
                if (tabData.isNotEmpty()) {
                    IndicatorAdaptiveTabRow(
                        background = ColorsTheme.colors.item,
                        tabs = tabData,
                        selectedTabIndex = selectIndex,
                        findTabText = { it.name },
                        onTabClick = { tabIndex ->
                            viewModel.dispatch(OfficialViewIntent.SelectedTabIndex(tabIndex))
                            coroutine.launch { pagerState.animateScrollToPage(tabIndex) }
                        }
                    )
                    HorizontalPager(count = tabData.size, state = pagerState) { page ->
                        OfficialListPage(navController = navController, tab = tabData[page])
                    }
                }
            }
        }
    }
}