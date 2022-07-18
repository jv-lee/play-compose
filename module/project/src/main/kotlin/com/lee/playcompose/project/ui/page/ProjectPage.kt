package com.lee.playcompose.project.ui.page

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
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import com.lee.playcompose.common.ui.widget.UiStatusPage
import com.lee.playcompose.project.R
import com.lee.playcompose.project.viewmodel.ProjectViewAction
import com.lee.playcompose.project.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch

/**
 * 项目页面
 * @author jv.lee
 * @date 2022/3/11
 */
@Composable
fun ProjectPage(
    navController: NavController = LocalNavController.current,
    viewModel: ProjectViewModel = viewModel()
) {
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val selectIndex = viewModel.viewStates.selectedIndex
    val tabData = viewModel.viewStates.tab
    val uiStatus = viewModel.viewStates.uiStatus

    LaunchedEffect(pagerState.currentPage) {
        viewModel.dispatch(ProjectViewAction.SelectedTabIndex(pagerState.currentPage))
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.project_title),
        elevation = 0.dp,
        navigationClick = { navController.popBackStack() }) {
        UiStatusPage(
            status = uiStatus,
            retry = { viewModel.dispatch(ProjectViewAction.RequestTabData) }) {
            Column {
                if (tabData.isNotEmpty()) {
                    IndicatorAdaptiveTabRow(
                        background = AppTheme.colors.item,
                        tabs = tabData,
                        selectedTabIndex = selectIndex,
                        findTabText = { it.name },
                        onTabClick = { tabIndex ->
                            viewModel.dispatch(ProjectViewAction.SelectedTabIndex(tabIndex))
                            coroutine.launch { pagerState.animateScrollToPage(tabIndex) }
                        },
                    )
                    HorizontalPager(count = tabData.size, state = pagerState) { page ->
                        ProjectListPage(navController = navController, tab = tabData[page])
                    }
                }
            }
        }
    }
}