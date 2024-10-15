package com.lee.playcompose.project.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.IndicatorAdaptiveTabRow
import com.lee.playcompose.common.ui.widget.UiStatusPage
import com.lee.playcompose.project.R
import com.lee.playcompose.project.viewmodel.ProjectViewIntent
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
    val selectIndex = viewModel.viewStates.selectedIndex
    val tabData = viewModel.viewStates.tab
    val uiStatus = viewModel.viewStates.uiStatus
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabData.size })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.dispatch(ProjectViewIntent.SelectedTabIndex(pagerState.currentPage))
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.project_title),
        elevation = 0.dp,
        navigationClick = { navController.popBackStack() }) {
        UiStatusPage(
            status = uiStatus,
            retry = { viewModel.dispatch(ProjectViewIntent.RequestTabData) }) {
            Column {
                if (tabData.isNotEmpty()) {
                    IndicatorAdaptiveTabRow(
                        background = ColorsTheme.colors.item,
                        tabs = tabData,
                        selectedTabIndex = selectIndex,
                        findTabText = { it.name },
                        onTabClick = { tabIndex ->
                            viewModel.dispatch(ProjectViewIntent.SelectedTabIndex(tabIndex))
                            coroutine.launch { pagerState.animateScrollToPage(tabIndex) }
                        },
                    )
                    HorizontalPager(state = pagerState) { page ->
                        ProjectListPage(navController = navController, tab = tabData[page])
                    }
                }
            }
        }
    }
}