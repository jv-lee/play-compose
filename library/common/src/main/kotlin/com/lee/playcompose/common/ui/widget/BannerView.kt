package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
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
@ExperimentalCoilApi
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
            contentPadding = PaddingValues(horizontal = if (clipCardEnable) 16.dp else 0.dp),
            modifier = Modifier
                .clickable { }
                .fillMaxSize()
                .pointerInput(pagerState.currentPage) {
                    awaitPointerEventScope {
                        // 当前控件优先处理事件再传递给子组件
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        // 获取第一个触摸点
                        val dragEvent = event.changes.firstOrNull() ?: return@awaitPointerEventScope
                        when {
                            // change事件已被消费
                            dragEvent.positionChangeConsumed() -> {
                                return@awaitPointerEventScope
                            }
                            // down 忽略已被消费事件
                            dragEvent.changedToDownIgnoreConsumed() -> {
                                // 记录当前页面索引
                                currentPageIndex = pagerState.currentPage
                            }
                            // up 忽略已被消费事件
                            dragEvent.changedToUpIgnoreConsumed() -> {
                                // 当前页面没有任何滚动/动画的时候pagerState.targetPage为null，处理单机事件
                                if (pagerState.targetPage == null) return@awaitPointerEventScope
                                // 当pageCount大于1，且手指抬起时如果页面没有改变，就手动触发动画
                                if (currentPageIndex == pagerState.currentPage && pagerState.pageCount > 1) {
                                    executeChangePage = !executeChangePage
                                }
                            }
                        }
                    }
                }) { page ->
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
                        .clickable {
                            onItemClick(item)
                        }
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
    val boxPadding = if (clipCardEnable) OffsetSmall else 0.dp

    Box(modifier = Modifier.padding(boxPadding)) {
        if (clipCardEnable) {
            Card(modifier = Modifier.fillMaxWidth()) { content() }
        } else {
            content()
        }
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
                        .clip(CircleShape)
                        .background(color)
                        .size(5.dp)
                )
                //指示点间的间隔
                if (index != data.lastIndex) {
                    Spacer(
                        modifier = Modifier
                            .height(0.dp)
                            .width(4.dp)
                    )
                }
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