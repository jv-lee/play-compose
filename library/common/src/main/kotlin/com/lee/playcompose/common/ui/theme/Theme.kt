package com.lee.playcompose.common.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.accompanist.insets.ProvideWindowInsets
import com.lee.playcompose.common.extensions.toast


private val DarkColorPalette = AppColors(
    primary = Color(0xFFD3D3D3),
    primaryDark = Color(0xFF6E6E6E),
    accent = Color(0xFFF9F9F9),
    focus = Color(0xFF874EFF),
    onFocus = Color(0xFFEADEFF),
    window = Color(0xFF010101),
    background = Color(0xFF010101),
    divider = Color(0x1A010101),
    item = Color(0xFF1B1B1B),
    shadow = Color(0xFF111111),
    placeholder = Color(0xFF575757),
)

private val LightColorPalette = AppColors(
    primary = Color(0xFF9B9B9B),
    primaryDark = Color(0xFF414141),
    accent = Color(0xFF191919),
    focus = Color(0xFF4100AB),
    onFocus = Color(0xFFEADEFF),
    window = Color(0xFFFFFFFF),
    background = Color(0xFFF9F5FF),
    divider = Color(0x1AF9F5FF),
    item = Color(0xFFFFFFFF),
    shadow = Color(0xFFEBEBEB),
    placeholder = Color(0xFFF9F9F9),
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
    divider: Color,
    item: Color,
    shadow: Color,
    placeholder: Color
) {
    var primary: Color by mutableStateOf(primary)
    var primaryDark: Color by mutableStateOf(primaryDark)
    var accent: Color by mutableStateOf(accent)
    var focus: Color by mutableStateOf(focus)
    var onFocus: Color by mutableStateOf(onFocus)
    var window: Color by mutableStateOf(window)
    var background: Color by mutableStateOf(background)
    var divider: Color by mutableStateOf(divider)
    var item: Color by mutableStateOf(item)
    var shadow: Color by mutableStateOf(shadow)
    var placeholder: Color by mutableStateOf(placeholder)
}

var LocalAppColors = compositionLocalOf {
    LightColorPalette
}

@Stable
object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current

    enum class Theme {
        Light, Dark
    }
}

@Composable
fun PlayComposeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    toast("isDark-$darkTheme")
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    val primary = animateColorAsState(colors.primary, TweenSpec(600))
    val primaryDark = animateColorAsState(colors.primaryDark, TweenSpec(600))
    val accent = animateColorAsState(colors.accent, TweenSpec(600))
    val focus = animateColorAsState(colors.focus, TweenSpec(600))
    val onFocus = animateColorAsState(colors.onFocus, TweenSpec(600))
    val window = animateColorAsState(colors.window, TweenSpec(600))
    val background = animateColorAsState(colors.background, TweenSpec(600))
    val divider = animateColorAsState(colors.divider, TweenSpec(600))
    val item = animateColorAsState(colors.item, TweenSpec(600))
    val shadow = animateColorAsState(colors.shadow, TweenSpec(600))
    val placeholder = animateColorAsState(colors.placeholder, TweenSpec(600))

    val appColors = AppColors(
        primary = primary.value,
        primaryDark = primaryDark.value,
        accent = accent.value,
        focus = focus.value,
        onFocus = onFocus.value,
        window = window.value,
        background = background.value,
        divider = divider.value,
        item = item.value,
        shadow = shadow.value,
        placeholder = placeholder.value,
    )

    CompositionLocalProvider(LocalAppColors provides appColors) {
        ProvideWindowInsets {
            MaterialTheme(
                typography = Typography,
                shapes = Shapes,
                content = content
            )
        }
    }
}