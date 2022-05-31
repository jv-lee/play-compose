package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 通用站位横竖线条组件
 * @author jv.lee
 * @date 2022/4/2
 */
@Composable
fun VerticallySpacer(color: Color = Color(0xFFC2C2C2)) {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(color = color)
    )
}

@Composable
fun HorizontallySpacer(color: Color = Color(0xFFC2C2C2)) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = color)
    )
}