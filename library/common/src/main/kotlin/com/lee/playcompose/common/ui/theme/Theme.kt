package com.lee.playcompose.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFFD3D3D3),
    primaryVariant = Color(0xFF6E6E6E),
    secondary = Color(0xFFF9F9F9),
    background = Color(0xFF010101),
    surface = Color(0xFF010101),
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF9B9B9B),
    primaryVariant = Color(0xFF414141),
    secondary = Color(0xFF191919),
    background = Color(0xFFF9F5FF),
    surface = Color(0xFFFFFFFF),

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun PlayComposeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}