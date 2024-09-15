package com.lee.playcompose.common.ui.widget.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.ToolBarHeight

/**
 * 项目公共appBar顶部title + action 组件
 * @param title 文案
 * @param actionPainter 右侧动作按钮
 * @param onActionClick 右侧动作按钮点击事件执行函数
 * @author jv.lee
 * @date 2022/3/3
 */
@Composable
fun AppTextActionBar(
    title: String,
    actionPainter: Painter,
    onActionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ToolBarHeight)
            .padding(start = OffsetLarge, end = OffsetLarge)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = FontSizeTheme.sizes.largeXX,
            color = ColorsTheme.colors.accent,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterEnd)
                .background(shape = CircleShape, color = ColorsTheme.colors.focus)
        ) {
            Image(
                painter = actionPainter,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { onActionClick() }
            )
        }
    }
}