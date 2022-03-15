package com.lee.playcompose.project.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.UiStatusPage
import com.lee.playcompose.project.R
import com.lee.playcompose.project.viewmodel.ProjectViewAction
import com.lee.playcompose.project.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/11
 * @description
 */
@Composable
fun ProjectPage(navController: NavController, viewModel: ProjectViewModel = viewModel()) {
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var selectIndex by remember { mutableStateOf(0) }

    val tabData = viewModel.viewStates.tab
    val pageStatus = viewModel.viewStates.pageStatus

    LaunchedEffect(pagerState.currentPage) {
        selectIndex = pagerState.currentPage
    }

    AppBarViewContainer(
        title = stringResource(id = R.string.project_title),
        elevation = 0.dp,
        navigationClick = { navController.popBackStack() }) {
        UiStatusPage(
            pageStatus,
            retry = { viewModel.dispatch(ProjectViewAction.RequestTabData) }) {
            Column {
                if (tabData.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = selectIndex,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = AppTheme.colors.item,
                        edgePadding = 0.dp
                    ) {
                        tabData.forEachIndexed { index, item ->
                            Tab(
                                selected = index == selectIndex,
                                text = { Text(text = item.name) },
                                onClick = {
                                    selectIndex = index
                                    coroutine.launch { pagerState.animateScrollToPage(index) }
                                })
                        }
                    }
                    HorizontalPager(count = tabData.size, state = pagerState) { page ->
                        ProjectListPage(navController = navController, tab = tabData[page])
                    }
                }
            }
        }
    }
}