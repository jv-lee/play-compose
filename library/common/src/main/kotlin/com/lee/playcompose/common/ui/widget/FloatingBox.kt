package com.lee.playcompose.common.ui.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

/**
 * 可拖拽悬浮Box容器
 * @param modifier 当前容器属性
 * @param type 拖拽回弹模式 [ReindexType]
 * @param limitBound 是否限制拖拽范围不超过父容器
 * @param shape 拖拽容器范围shape 点击回馈范围
 * @param content 子view
 * @author jv.lee
 * @date 2022/5/17
 */
@Composable
fun FloatingBox(
    modifier: Modifier = Modifier,
    @ReindexType type: Int = ReindexType.MOVE,
    limitBound: Boolean = false,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current.density
    var scope by remember {
        mutableStateOf(FloatingBoxScope(density = density, type = type, limitBound = limitBound))
    }
    val offsetXAnimate by animateDpAsState(targetValue = scope.offsetX)
    val offsetYAnimate by animateDpAsState(targetValue = scope.offsetY)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                // 记录父容器 offset x y 位于屏幕坐标
                scope = scope.copy(parentOffset = it.localToWindow(Offset.Zero))
                // 记录父容器 rect l t r b 位于屏幕坐标
                scope = scope.copy(parentRect = scope.parentRect())
            }
            .onSizeChanged {
                // 记录父容器 size width height
                scope = scope.copy(parentSize = it.toSize())
                // 记录父容器 rect l t r b 位于屏幕坐标
                scope = scope.copy(parentRect = scope.parentRect())
            }
    ) {
        Card(
            shape = shape,
            backgroundColor = Color.Transparent,
            contentColor = Color.Transparent,
            elevation = 0.dp,
            modifier = modifier
                .onGloballyPositioned {
                    // 记录当前view offset x y 位于屏幕坐标
                    scope = scope.copy(localOffset = it.localToWindow(Offset.Zero))
                }
                .onSizeChanged {
                    // 记录当前view size width height
                    scope = scope.copy(localSize = it.toSize())
                }
                .offset(offsetXAnimate, offsetYAnimate)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = { scope = scope.reindexScope() },
                        onDragCancel = { scope = scope.reindexScope() },
                        onDrag = { _, dragAmount ->
                            scope = scope.updateOffsetXY(dragAmount.x, dragAmount.y)
                        }
                    )
                }
        ) {
            content()
        }
    }
}

annotation class ReindexType {
    companion object {
        const val MOVE = 0 // 不限制复位自由摆放
        const val REINDEX_XY = 1 // x，y轴同时开启复位
        const val REINDEX_X = 2 // x轴开启复位
        const val REINDEX_Y = 3 // y轴开启复位
    }
}

private data class FloatingBoxScope(
    val density: Float = 0f,
    @ReindexType val type: Int = ReindexType.MOVE,
    val limitBound: Boolean = false,
    val parentSize: Size = Size.Zero,
    val localSize: Size = Size.Zero,
    val parentOffset: Offset = Offset.Zero,
    val localOffset: Offset = Offset.Zero,
    val parentRect: Rect = Rect.Zero,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp
) {

    // 更新当前view位置
    fun updateOffsetXY(offsetX: Float, offsetY: Float): FloatingBoxScope {
        var x = this.offsetX + offsetX.dp()
        var y = this.offsetY + offsetY.dp()

        // 限制上下左右边界移动
        if (limitBound) {
            if ((localOffset.x + localSize.width) + x.px() > parentRect.right) {
                x = (parentRect.right - (localOffset.x + localSize.width)).dp()
            } else if (localOffset.x + x.px() < parentRect.left) {
                x = (parentRect.left - localOffset.x).dp()
            }

            if ((localOffset.y + localSize.height) + y.px() > parentRect.bottom) {
                y = (parentRect.bottom - (localOffset.y + localSize.height)).dp()
            } else if (localOffset.y + y.px() < parentRect.top) {
                y = (parentRect.top - localOffset.y).dp()
            }
        }

        return copy(offsetX = x, offsetY = y)
    }

    // 根据模式重置view位置
    fun reindexScope(): FloatingBoxScope {
        return when (type) {
            ReindexType.MOVE -> reindexMove()
            ReindexType.REINDEX_X -> reindexX()
            ReindexType.REINDEX_Y -> reindexY()
            ReindexType.REINDEX_XY -> reindexXY()
            else -> this
        }
    }

    fun parentRect() = Rect(
        left = parentOffset.x,
        top = parentOffset.y,
        right = parentOffset.x + parentSize.width,
        bottom = parentOffset.y + parentSize.height
    )

    private fun Dp.px() = value * density

    private fun Float.dp() = (this / density).dp

    private fun reindexMove(): FloatingBoxScope {
        var x = offsetX
        var y = offsetY

        // 超出父容器右边界
        if ((localOffset.x + localSize.width) + offsetX.px() > parentRect.right) {
            x = (parentRect.right - (localOffset.x + localSize.width)).dp()
            // 超出父容器左边界
        } else if (localOffset.x + offsetX.px() < parentRect.left) {
            x = (parentRect.left - localOffset.x).dp()
        }

        // 超出父容器下边界
        if ((localOffset.y + localSize.height) + offsetY.px() > parentRect.bottom) {
            y = (parentRect.bottom - (localOffset.y + localSize.height)).dp()
            // 超出父容器上边界
        } else if (localOffset.y + offsetY.px() < parentRect.top) {
            y = (parentRect.top - localOffset.y).dp()
        }

        return copy(offsetX = x, offsetY = y)
    }

    private fun reindexX(): FloatingBoxScope {
        val viewCenter = localOffset.x + localSize.width / 2
        val parentCenter = parentOffset.x + parentSize.width / 2
        val isRight = viewCenter > parentCenter

        var y = offsetY
        val x = if (isRight) {
            if (viewCenter + offsetX.px() > parentCenter) 0.dp
            else -(localOffset.x - parentRect.left).dp()
        } else {
            if (viewCenter + offsetX.px() < parentCenter) 0.dp
            else (parentRect.right - (localOffset.x + localSize.width)).dp()
        }

        // 超出父容器下边界
        if ((localOffset.y + localSize.height) + offsetY.px() > parentRect.bottom) {
            y = (parentRect.bottom - (localOffset.y + localSize.height)).dp()
            // 超出父容器上边界
        } else if (localOffset.y + offsetY.px() < parentRect.top) {
            y = (parentRect.top - localOffset.y).dp()
        }

        return copy(offsetX = x, offsetY = y)
    }

    private fun reindexY(): FloatingBoxScope {
        val viewCenter = localOffset.y + localSize.height / 2
        val parentCenter = parentOffset.y + parentSize.height / 2
        val isBottom = viewCenter > parentCenter

        var x = offsetX
        val y = if (isBottom) {
            if (viewCenter + offsetY.px() > parentCenter) 0.dp
            else -(localOffset.y - parentRect.top).dp()
        } else {
            if (viewCenter + offsetY.px() < parentCenter) 0.dp
            else (parentRect.bottom - (localOffset.y + localSize.height)).dp()
        }

        // 超出父容器右边界
        if ((localOffset.x + localSize.width) + offsetX.px() > parentRect.right) {
            x = (parentRect.right - (localOffset.x + localSize.width)).dp()
            // 超出父容器左边界
        } else if (localOffset.x + offsetX.px() < parentRect.left) {
            x = (parentRect.left - localOffset.x).dp()
        }

        return copy(offsetX = x, offsetY = y)
    }

    private fun reindexXY(): FloatingBoxScope {
        return copy(offsetX = 0f.dp, offsetY = 0.dp)
    }
}
