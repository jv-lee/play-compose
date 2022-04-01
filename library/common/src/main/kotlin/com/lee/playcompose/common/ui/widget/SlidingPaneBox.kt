package com.lee.playcompose.common.ui.widget

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @author jv.lee
 * @date 2022/4/1
 * @description 侧滑菜单Box容器
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SlidingPaneBox(
    modifier: Modifier = Modifier,
    slidingWidth: Dp = 80.dp,
    slidingAlign: Alignment = Alignment.CenterEnd,
    sliding: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var offsetX by remember { mutableStateOf(0.dp) }
    val offsetXAnimate by animateDpAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
    )

    val dragOut =
        if (slidingAlign == Alignment.CenterStart) {
            { offsetX = if (offsetX > slidingWidth / 2) slidingWidth else 0.dp }
        } else {
            { offsetX = if (offsetX < -(slidingWidth / 2)) -slidingWidth else 0.dp }
        }

    Box(modifier = modifier) {
        // sliding layout
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(slidingWidth)
                .align(slidingAlign)
        ) {
            sliding()
        }

        // content layout
        Box(modifier = Modifier
            .fillMaxSize()
            .offset(x = offsetXAnimate)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = dragOut,
                    onDragCancel = dragOut,
                    onHorizontalDrag = { _, dragAmount ->
                        val endOffsetX = offsetX + dragAmount.dp
                        if (slidingAlign == Alignment.CenterStart) {
                            when {
                                endOffsetX >= slidingWidth -> {
                                    offsetX = slidingWidth
                                }
                                endOffsetX <= 0.dp -> {
                                    offsetX = 0.dp
                                }
                                else -> {
                                    offsetX += dragAmount.dp
                                }
                            }
                        } else {
                            when {
                                endOffsetX <= -slidingWidth -> {
                                    offsetX = -slidingWidth
                                }
                                endOffsetX >= 0.dp -> {
                                    offsetX = 0.dp
                                }
                                else -> {
                                    offsetX += dragAmount.dp
                                }
                            }
                        }
                    })
            }) {
            content()
        }
    }
}