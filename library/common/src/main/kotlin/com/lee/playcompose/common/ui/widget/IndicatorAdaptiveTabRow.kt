package com.lee.playcompose.common.ui.widget

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.ColorsTheme

/**
 * 自适应指示器宽度TabRow
 * @param tabs 数据源
 * @param selectedTabIndex 默认选中tabIndex
 * @param findTabText 对外提供查询数据源T中的tab文案高阶函数
 * @param onTabClick tab点击事件回调
 * @param background tabLayout背景颜色
 * @author jv.lee
 * @date 2022/3/30
 */
@Composable
fun <T> IndicatorAdaptiveTabRow(
    tabs: List<T>,
    selectedTabIndex: Int,
    findTabText: (T) -> String,
    onTabClick: (Int) -> Unit,
    background: Color = MaterialTheme.colorScheme.primary
) {
    if (tabs.isEmpty()) return
    val density = LocalDensity.current
    val tabWidths = remember {
        mutableStateListOf<Dp>().apply { repeat(tabs.size) { add(0.dp) } }
    }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth(),
        containerColor = background,
        edgePadding = 0.dp,
        divider = {},
        indicator = { tabPositions ->
            val tabPosition = if (tabPositions.size > selectedTabIndex) {
                tabPositions[selectedTabIndex]
            } else tabPositions[0]
            val tabWidth = if (tabWidths.size > selectedTabIndex) {
                tabWidths[selectedTabIndex]
            } else tabWidths[0]
            Box(
                modifier = Modifier
                    .adaptiveTabIndicatorOffset(
                        currentTabPosition = tabPosition,
                        tabWidth = tabWidth
                    )
                    .height(3.dp)
                    .background(color = ColorsTheme.colors.accent)
            )
        }
    ) {
        tabs.forEachIndexed { index, item ->
            val tabTextColor =
                if (index == selectedTabIndex) ColorsTheme.colors.accent else ColorsTheme.colors.primary
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabClick(index) },
                text = {
                    Text(
                        text = findTabText(item),
                        color = tabTextColor,
                        onTextLayout = { textLayoutResult ->
                            if (tabWidths.size > index) {
                                tabWidths[index] =
                                    with(density) { textLayoutResult.size.width.toDp() }
                            }
                        })
                }
            )
        }
    }
}

private fun Modifier.adaptiveTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "IndicatorAdaptiveTabRow"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}