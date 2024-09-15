package com.lee.playcompose.common.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Compose页面 颜色主题
 *
 * 主题色新增
 * 1. [AppColors] 添加颜色变量
 * 2. [LightColorPalette] [DarkColorPalette] 添加相应变量名白天/黑夜颜色
 * 3. [changeColorAnimate] 添加颜色变量动画值
 *
 * 主题色使用
 * [ColorsTheme.colors] 调用相应颜色使用
 *
 * @author jv.lee
 * @date 2024/8/21
 */
@Stable
object ColorsTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

private var LocalAppColors = compositionLocalOf { LightColorPalette }

private val DarkColorPalette = AppColors(
    primary = Color(0xFFD3D3D3),
    primaryDark = Color(0xFF6E6E6E),
    accent = Color(0xFFF9F9F9),
    focus = Color(0xFF874EFF),
    onFocus = Color(0xFFEADEFF),
    window = Color(0xFF010101),
    background = Color(0xFF010101),
    backgroundTransparent = Color(0x1F010101),
    divider = Color(0x1A010101),
    item = Color(0xFF1B1B1B),
    shadow = Color(0xFF111111),
    placeholder = Color(0xFF575757),
    label = Color(0xFF575757)
)

private val LightColorPalette = AppColors(
    primary = Color(0xFF9B9B9B),
    primaryDark = Color(0xFF414141),
    accent = Color(0xFF191919),
    focus = Color(0xFF4100AB),
    onFocus = Color(0xFFEADEFF),
    window = Color(0xFFFFFFFF),
    background = Color(0xFFF9F5FF),
    backgroundTransparent = Color(0x1FF9F5FF),
    divider = Color(0x1AF9F5FF),
    item = Color(0xFFFFFFFF),
    shadow = Color(0xFFEBEBEB),
    placeholder = Color(0xFFF9F9F9),
    label = Color(0xFFEBEBEB)
)

@Stable
class AppColors(
    primary: Color,
    primaryDark: Color,
    accent: Color,
    focus: Color,
    onFocus: Color,
    window: Color,
    background: Color,
    backgroundTransparent: Color,
    divider: Color,
    item: Color,
    shadow: Color,
    placeholder: Color,
    label: Color
) {
    var primary: Color by mutableStateOf(primary)
    var primaryDark: Color by mutableStateOf(primaryDark)
    var accent: Color by mutableStateOf(accent)
    var focus: Color by mutableStateOf(focus)
    var onFocus: Color by mutableStateOf(onFocus)
    var window: Color by mutableStateOf(window)
    var background: Color by mutableStateOf(background)
    var backgroundTransparent: Color by mutableStateOf(backgroundTransparent)
    var divider: Color by mutableStateOf(divider)
    var item: Color by mutableStateOf(item)
    var shadow: Color by mutableStateOf(shadow)
    var placeholder: Color by mutableStateOf(placeholder)
    var label: Color by mutableStateOf(label)
}


/**
 * 根据当前主题获取相应主题颜色设置动画状态值动态颜色切换
 *
 * @param isNightMode 是否为黑夜模式
 */
@Composable
private fun changeColorAnimate(isNightMode: Boolean): AppColors {
    val colors = if (isNightMode) DarkColorPalette else LightColorPalette

    @Composable
    fun animateStateValue(color: Color): Color =
        animateColorAsState(color, TweenSpec(600), label = "").value

    return AppColors(
        primary = animateStateValue(colors.primary),
        primaryDark = animateStateValue(colors.primaryDark),
        accent = animateStateValue(colors.accent),
        focus = animateStateValue(colors.focus),
        onFocus = animateStateValue(colors.onFocus),
        window = animateStateValue(colors.window),
        background = animateStateValue(colors.background),
        backgroundTransparent = animateStateValue(colors.backgroundTransparent),
        divider = animateStateValue(colors.divider),
        item = animateStateValue(colors.item),
        shadow = animateStateValue(colors.shadow),
        placeholder = animateStateValue(colors.placeholder),
        label = animateStateValue(colors.label),
    )
}

/**
 * 颜色主题ComposeView容器提供者
 * @param isNightMode 是否为黑夜模式
 * @param content 子Composable内容
 */
@Composable
fun ColorsThemeProvider(isNightMode: Boolean, content: @Composable () -> Unit) {
    val colors = changeColorAnimate(isNightMode = isNightMode)
    CompositionLocalProvider(LocalAppColors provides colors) {
        content()
    }
}
