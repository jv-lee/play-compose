package com.lee.playcompose.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compose页面 像素值
 *
 * 主题字体大小新增
 * [AppFontSizes] 添加字体大小变量
 *
 * 主题字体大小使用
 * [FontSizeTheme.sizes] 调用相应颜色使用
 *
 * @author jv.lee
 * @date 2024/8/21
 */
@Stable
object FontSizeTheme {
    val sizes: AppFontSizes
        @Composable
        get() = LocalFontSizes.current
}

private var LocalFontSizes = compositionLocalOf { AppFontSizes() }

@Stable
class AppFontSizes(
    largeXX: TextUnit = 20.sp,
    largeX: TextUnit = 18.sp,
    large: TextUnit = 16.sp,
    medium: TextUnit = 14.sp,
    small: TextUnit = 12.sp,
    smallX: TextUnit = 10.sp
) {
    var largeXX: TextUnit by mutableStateOf(largeXX)
    var largeX: TextUnit by mutableStateOf(largeX)
    var large: TextUnit by mutableStateOf(large)
    var medium: TextUnit by mutableStateOf(medium)
    var small: TextUnit by mutableStateOf(small)
    var smallX: TextUnit by mutableStateOf(smallX)
}

@Composable
fun FontSizeThemeProvider(scale: Float, content: @Composable () -> Unit) {
    val fontSizes = AppFontSizes(
        small = FontSizeTheme.sizes.small * scale,
        medium = FontSizeTheme.sizes.medium * scale,
    )
    CompositionLocalProvider(LocalFontSizes provides fontSizes) {
        content()
    }
}

// 全局通用组件数值
val ToolBarHeight = 56.dp
val NavigationBarHeight = 56.dp
val ListStateItemHeight = 60.dp
val TabBarHeight = 36.dp

// 全局通用radius值
val OffsetRadiusLarge = 32.dp
val OffsetRadiusMedium = 16.dp
val OffsetRadiusSmall = 8.dp

// 全局通用样式 （统一全局margin padding radius值）
val OffsetLargeMax = 32.dp
val OffsetLarge = 16.dp
val OffsetMedium = 8.dp
val OffsetSmall = 4.dp

// 通用圆角线宽度/阴影
val BorderWidth = 2.dp
val ShadowSmall = 4.dp


val ToolBarTitleSize = 18.sp

val cardCorner = 5.dp // 卡片的圆角
val buttonCorner = 3.dp // 按钮的圆角
val buttonHeight = 36.dp // 按钮的高度
val shadowElevation = 10.dp
