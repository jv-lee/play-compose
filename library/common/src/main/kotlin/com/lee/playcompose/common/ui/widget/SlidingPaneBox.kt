package com.lee.playcompose.common.ui.widget

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 侧滑菜单Box容器
 * @param modifier 样式属性
 * @param slidingWidth 侧滑区域宽度
 * @param slidingAlign 侧滑菜单位置
 * @param state 侧滑状态
 * @param sliding 侧滑composable元素
 * @param content 容器composable元素
 * @author jv.lee
 * @date 2022/4/1
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SlidingPaneBox(
    modifier: Modifier = Modifier,
    slidingWidth: Dp = 80.dp,
    slidingAlign: Alignment = Alignment.CenterEnd,
    state: SlidingPaneState = SlidingPaneState(),
    sliding: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var offsetX by remember { mutableStateOf(0.dp) }
    val offsetXAnimate by animateDpAsState(targetValue = offsetX, label = "")

    val closeAction = {
        state.expand = false
        offsetX = 0.dp
    }
    val openAction = {
        offsetX = if (slidingAlign == Alignment.CenterStart) slidingWidth else -slidingWidth
        state.expand = true
        state.closeAction = closeAction
    }
    val dragOut = { if (abs(offsetX.value).dp > slidingWidth / 2) openAction() else closeAction() }

    Box(modifier = modifier) {
        // sliding layout
        if (abs(offsetXAnimate.value) > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(slidingWidth)
                    .align(slidingAlign)
            ) {
                sliding()
            }
        }

        // content layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = offsetXAnimate)
                .pointerInteropFilter { event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        if (state.expand) {
                            state.expand = false
                            state.closeAction()
                            return@pointerInteropFilter true
                        }
                    }
                    false
                }
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
                                        offsetX += dragAmount.toDp()
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
                                        offsetX += dragAmount.toDp()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}

data class SlidingPaneState(var expand: Boolean = false, var closeAction: () -> Unit = {})

@Composable
fun rememberSlidingPaneState(): MutableState<SlidingPaneState> = remember {
    mutableStateOf(SlidingPaneState())
}

@Stable
fun Modifier.slidingPaneState(state: SlidingPaneState) =
    this.then(
        pointerInteropFilter { event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (state.expand) {
                    state.expand = false
                    state.closeAction()
                    return@pointerInteropFilter true
                }
            }
            false
        }
    )