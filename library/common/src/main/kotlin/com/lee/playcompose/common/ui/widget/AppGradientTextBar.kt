package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeLargeXX
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.ToolBarHeight

/**
 * 公共appBar顶部渐变色导航title组件
 * @author jv.lee
 * @date 2022/3/3
 */
@Composable
fun AppGradientTextBar(title: String, navigationPainter: Painter, onNavigationClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ToolBarHeight)
            .padding(start = OffsetLarge, end = OffsetLarge)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = FontSizeLargeXX,
            color = AppTheme.colors.accent,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterEnd)
                .background(shape = CircleShape, color = AppTheme.colors.focus)
        ) {
            Image(
                painter = navigationPainter,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { onNavigationClick() }
            )
        }
    }
}