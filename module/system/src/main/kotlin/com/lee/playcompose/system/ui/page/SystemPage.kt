package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.AppBarContainer
import com.lee.playcompose.system.R
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun SystemPage(navController: NavController, paddingValues: PaddingValues) {
    val pagerState = rememberPagerState()
    Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
        // pageContent
        HorizontalPager(count = 2, state = pagerState, userScrollEnabled = false) { page ->
            when (page) {
                0 -> SystemContentPage(navController)
                1 -> NavigationContentPage(navController)
            }
        }

        // header
        AppBarContainer {
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
    val color = if (isSelected) AppTheme.colors.focus else AppTheme.colors.onFocus
    val textColor = if (isSelected) AppTheme.colors.onFocus else AppTheme.colors.focus
    Box(
        modifier = Modifier
            .size(width = SystemTabWidth, height = SystemTabHeight)
            .background(color = color, shape = RoundedCornerShape(SystemTabRadius))
            .wrapContentSize(Alignment.Center)
            .clickable { onClick() }
    ) {
        Text(text = text, fontSize = FontSizeMedium, color = textColor)
    }
}

