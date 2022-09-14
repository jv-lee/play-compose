@file:OptIn(ExperimentalCoilApi::class, ExperimentalComposeUiApi::class)

package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.*
import kotlinx.coroutines.delay

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
    loopEnable: Boolean = true,
) {
    //无限翻页最大倍数 count * filed
    val looperCountFactor = 500
    val pagerState =
        rememberPagerState(initialPage = getStartSelectItem(data.size, looperCountFactor))

    var executeChangePage by remember { mutableStateOf(false) }
    var currentPageIndex = 0

    // auto scroll
    LaunchedEffect(pagerState.currentPage, executeChangePage, loopEnable) {
        if (pagerState.pageCount > 0 && loopEnable) {
            delay(timeMillis = timeMillis)
            var itemIndex = pagerState.currentPage
            if (itemIndex == looperCountFactor * 3 - 1) {
                val startIndex = getStartSelectItem(data.size, looperCountFactor)
                pagerState.animateScrollToPage(startIndex)
            } else {
                ++itemIndex
                pagerState.animateScrollToPage(itemIndex)
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            count = looperCountFactor * 3,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = if (clipCardEnable) OffsetLargeMax else 0.dp),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            // 当pageCount大于1，事件进入等待，就手动触发动画
                            if (currentPageIndex == pagerState.currentPage && pagerState.pageCount > 1) {
                                executeChangePage = !executeChangePage
                            }

                            // 当前控件优先处理事件再传递给子组件
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            // 获取第一个触摸点
                            val dragEvent =
                                event.changes.firstOrNull() ?: return@awaitPointerEventScope
                            // change事件已被消费
                            if (dragEvent.isConsumed) return@awaitPointerEventScope
                            when {
                                dragEvent.changedToDown() -> {
                                    // 记录当前页面索引
                                    currentPageIndex = pagerState.currentPage
                                }
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
            data = data, state = pagerState, modifier = Modifier
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
            color = AppTheme.colors.background,
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

private fun getRealIndex(position: Int, size: Int): Int {
    return position % size
}

private fun getStartSelectItem(size: Int, looperCount: Int): Int {
    // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
    // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
    var currentItem: Int = size * looperCount / 2
    val realIndex = getRealIndex(currentItem, size)
    if (realIndex == 0) {
        return currentItem
    }
    // 直到找到从0开始的位置
    while (realIndex != 0) {
        currentItem++
    }
    return currentItem
}