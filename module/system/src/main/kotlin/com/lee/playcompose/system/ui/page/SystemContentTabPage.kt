package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/14
 * @description
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
                ScrollableTabRow(
                    selectedTabIndex = selectIndex,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = AppTheme.colors.item,
                    edgePadding = 0.dp
                ) {
                    parentTab.children.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectIndex,
                            text = { Text(text = item.name) },
                            onClick = {
                                selectIndex = index
                                coroutine.launch { pagerState.animateScrollToPage(index) }
                            })
                    }
                }
                HorizontalPager(count = parentTab.children.size, state = pagerState) { page ->
                    val item = parentTab.children[page]
                    SystemContentListPage(navController = navController, tab = item)
                }
            }
        }
    }
}
