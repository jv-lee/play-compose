@file:OptIn(ExperimentalCoilApi::class)

package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.OffsetLargeMax
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * banner组件
 * @param modifier 样式属性配置
 * @param data 构建数据集合
 * @param findPath 提供给外部执行[T]的path查找函数
 * @param onItemClick 每一项点击执行函数
 * @param timeMillis 切换banner间隔时间（毫秒）
 * @param indicatorAlignment banner指示器位置
 * @param clipCardEnable banner内容是否添加padding间隔
 * @author jv.lee
 * @date 2022/3/2
 */
@Composable
fun <T : Any> BannerView(
    modifier: Modifier = Modifier,
    data: List<T>,
    findPath: ((T) -> String),
    onItemClick: ((T) -> Unit),
    timeMillis: Long = 3000,
    indicatorAlignment: Alignment = Alignment.BottomCenter,
    clipCardEnable: Boolean = true,
    loopEnable: Boolean = true
) {
    val pagerState = rememberPagerState(
        initialPage = getStartSelectItem(data.size),
        pageCount = { getRealCount() }
    )
    var executeChangePage by remember { mutableStateOf(false) }
    var currentPageIndex = 0

    // auto scroll
    if (pagerState.pageCount > 0 && loopEnable) {
        var indexKey by remember { mutableIntStateOf(0) }
        LaunchedEffect(indexKey, executeChangePage) {
            launch {
                delay(timeMillis = timeMillis)
                // 滚动到最后一个时直接切换到开始index
                if (pagerState.currentPage == getRealCount() - 1) {
                    val startIndex = getStartSelectItem(data.size)
                    pagerState.scrollToPage(startIndex)
                } else {
                    // 计算出下一个自动轮播index
                    val nextIndex = (pagerState.currentPage + 1).mod(pagerState.pageCount)
                    pagerState.animateScrollToPage(nextIndex)
                    indexKey = nextIndex
                }
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = if (clipCardEnable) OffsetLargeMax else 0.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        // 当pageCount大于1，事件进入等待，就手动触发动画 (手动滚动到当前页面，或下一个页面都重新触发动画)
                        if ((currentPageIndex == pagerState.currentPage
                                    || currentPageIndex == pagerState.currentPage - 1)
                            && pagerState.pageCount > 1
                        ) {
                            executeChangePage = !executeChangePage
                        }

                        // 当前控件优先处理事件再传递给子组件
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        // 获取第一个触摸点
                        val dragEvent =
                            event.changes.firstOrNull() ?: return@awaitEachGesture
                        // change事件已被消费
                        if (dragEvent.isConsumed) return@awaitEachGesture
                        when {
                            dragEvent.changedToDown() -> {
                                // 记录当前页面索引
                                currentPageIndex = pagerState.currentPage
                            }
                        }
                    }
                }
        ) { page ->
            val index = getRealIndex(page, data.size)
            val item = data[index]
            val path = findPath(item)

            BannerItemContainer(clipCardEnable = clipCardEnable) {
                Image(
                    painter = rememberImagePainter(data = path),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onItemClick(item) }
                )
            }
        }

        // indicator
        BannerIndicator(
            data = data,
            state = pagerState,
            modifier = Modifier
                .align(indicatorAlignment)
                .padding(OffsetMedium)
        )
    }
}

@Composable
private fun BannerItemContainer(
    clipCardEnable: Boolean,
    content: @Composable () -> Unit
) {
    if (clipCardEnable) {
        Surface(
            modifier = Modifier
                .padding(OffsetSmall)
                .fillMaxSize(),
            shape = RoundedCornerShape(OffsetRadiusMedium),
            color = ColorsTheme.colors.background,
            elevation = OffsetSmall,
            content = content
        )
    } else {
        content()
    }
}

@Composable
private fun <T : Any> BannerIndicator(
    modifier: Modifier = Modifier,
    data: List<T>,
    state: PagerState
) {
    Box(modifier) {
        Row {
            val page = getRealIndex(state.currentPage, data.size)
            for (index in data.indices) {
                val color = if (index == page) Color.White else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(OffsetSmall)
                        .clip(CircleShape)
                        .background(color)
                        .size(5.dp)
                )
            }
        }
    }
}

private const val looperCountFactor = 500

private fun getRealIndex(position: Int, size: Int) = position % size

private fun getRealCount() = looperCountFactor * 3

private fun getStartSelectItem(size: Int): Int {
    // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
    // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
    var currentItem: Int = size * looperCountFactor / 2

    // 直到找到从0开始的位置
    while (getRealIndex(currentItem, size) != 0) {
        currentItem++
    }
    return currentItem
}