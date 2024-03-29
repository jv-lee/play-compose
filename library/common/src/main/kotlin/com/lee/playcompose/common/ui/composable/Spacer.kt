/*
 * 通用占位组件
 * @author jv.lee
 * @date 2022/3/8
 */
package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.NavigationBarHeight
import com.lee.playcompose.common.ui.theme.ToolBarHeight

/**
 * 通用主页头部占位组件
 */
@Composable
fun HeaderSpacer() {
    Spacer(
        modifier = Modifier
            .height(ToolBarHeight + WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    )
}

/**
 * 通用主页底部占位组件
 */
@Composable
fun FooterSpacer() {
    Spacer(modifier = Modifier.height(NavigationBarHeight))
}

/**
 * 垂直直线占位组件
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

/**
 * 水平直线占位组件
 */
@Composable
fun HorizontallySpacer(color: Color = Color(0xFFC2C2C2)) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = color)
    )
}