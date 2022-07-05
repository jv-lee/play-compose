/*
 * 全局 CompositionLocalProvider 扩展函数
 * @author jv.lee
 * @date 2022/3/18
 */
@file:OptIn(ExperimentalFoundationApi::class)

package com.lee.playcompose.base.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.fragment.app.FragmentActivity


val LocalActivity = staticCompositionLocalOf<FragmentActivity> {
    noLocalProvidedFor("LocalActivity")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

@Composable
fun FragmentActivity.ProviderActivity(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalActivity provides this) {
        content()
    }
}

@Composable
fun ProviderOverScroll(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalOverScrollConfiguration provides null) {
        content()
    }
}

@Composable
fun ProviderDensity(width: Float = 360f, content: @Composable () -> Unit) {
    val fontScale = LocalDensity.current.fontScale
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels

    CompositionLocalProvider(
        LocalDensity provides Density(density = widthPixels / width, fontScale = fontScale)
    ) {
        content()
    }
}