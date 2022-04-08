package com.lee.playcompose.official.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import com.lee.playcompose.common.ui.widget.UiStatusPage
import com.lee.playcompose.official.R
import com.lee.playcompose.official.viewmodel.OfficialViewAction
import com.lee.playcompose.official.viewmodel.OfficialViewModel
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/11
 * @description
 */
@Composable
fun OfficialPage(navController: NavController, viewModel: OfficialViewModel = viewModel()) {
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var selectIndex by remember { mutableStateOf(0) }

    val tabData = viewModel.viewStates.tab
    val pageStatus = viewModel.viewStates.pageStatus

    LaunchedEffect(pagerState.currentPage) {
        selectIndex = pagerState.currentPage
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.official_title),
        elevation = 0.dp,
        navigationClick = { navController.popBackStack() }) {
        UiStatusPage(
            status = pageStatus,
            retry = { viewModel.dispatch(OfficialViewAction.RequestTabData) }) {
            Column {
                if (tabData.isNotEmpty()) {
                    IndicatorAdaptiveTabRow(
                        background = AppTheme.colors.item,
                        tabs = tabData,
                        selectedTabIndex = selectIndex,
                        findTabText = { it.name },
                        onTabClick = { tabIndex ->
                            selectIndex = tabIndex
                            coroutine.launch { pagerState.animateScrollToPage(tabIndex) }
                        },
                    )
                    HorizontalPager(count = tabData.size, state = pagerState) { page ->
                        OfficialListPage(navController = navController, tab = tabData[page])
                    }
                }
            }
        }
    }
}