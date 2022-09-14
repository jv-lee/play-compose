package com.lee.playcompose.common.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lee.playcompose.base.extensions.ProviderActivity
import com.lee.playcompose.base.extensions.ProviderNavController
import com.lee.playcompose.base.extensions.ProviderOverScroll
import com.lee.playcompose.base.extensions.activityViewModel
import com.lee.playcompose.common.viewmodel.ThemeViewModel


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
    label: Color,
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

var LocalAppColors = compositionLocalOf {
    LightColorPalette
}

@Stable
object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

@Composable
fun FragmentActivity.PlayComposeTheme(
    viewModel: ThemeViewModel = activityViewModel(this),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val viewState = viewModel.viewStates
    val colors = if (viewState.isDark) DarkColorPalette else LightColorPalette

    val primary = animateColorAsState(colors.primary, TweenSpec(600))
    val primaryDark = animateColorAsState(colors.primaryDark, TweenSpec(600))
    val accent = animateColorAsState(colors.accent, TweenSpec(600))
    val focus = animateColorAsState(colors.focus, TweenSpec(600))
    val onFocus = animateColorAsState(colors.onFocus, TweenSpec(600))
    val window = animateColorAsState(colors.window, TweenSpec(600))
    val background = animateColorAsState(colors.background, TweenSpec(600))
    val backgroundTransparent = animateColorAsState(colors.backgroundTransparent, TweenSpec(600))
    val divider = animateColorAsState(colors.divider, TweenSpec(600))
    val item = animateColorAsState(colors.item, TweenSpec(600))
    val shadow = animateColorAsState(colors.shadow, TweenSpec(600))
    val placeholder = animateColorAsState(colors.placeholder, TweenSpec(600))
    val label = animateColorAsState(colors.label, TweenSpec(600))

    systemUiController.statusBarDarkContentEnabled = viewState.statusBarDarkContentEnabled
    systemUiController.setNavigationBarColor(window.value)

    val appColors = AppColors(
        primary = primary.value,
        primaryDark = primaryDark.value,
        accent = accent.value,
        focus = focus.value,
        onFocus = onFocus.value,
        window = window.value,
        background = background.value,
        backgroundTransparent = backgroundTransparent.value,
        divider = divider.value,
        item = item.value,
        shadow = shadow.value,
        placeholder = placeholder.value,
        label = label.value
    )

    val typography = Typography(
        body1 = TextStyle(
            color = accent.value,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )

    CompositionLocalProvider(LocalAppColors provides appColors) {
        ProviderActivity {
            ProviderNavController {
                ProviderOverScroll {
                    MaterialTheme(
                        typography = typography,
                        shapes = Shapes,
                        content = content
                    )
                }
            }
        }
    }
}