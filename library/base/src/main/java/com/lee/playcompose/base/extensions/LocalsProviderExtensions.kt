/*
 * 全局 CompositionLocalProvider 扩展函数
 * @author jv.lee
 * @date 2022/3/18
 */
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)

package com.lee.playcompose.base.extensions

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

/**
 * 全局唯一Activity实例 任何组件获取Activity引用通过该引用
 */
val LocalActivity = staticCompositionLocalOf<FragmentActivity> {
    noLocalProvidedFor("LocalActivity")
}

/**
 * 全局唯一NavController实例 任何组件通过该NavController控制页面视图导航
 */
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    noLocalProvidedFor("LocalNavController")
}

/**
 * 全局Activity引用实力Provider
 * @param content 组件内容
 */
@Composable
fun FragmentActivity.ProviderActivity(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalActivity provides this) {
        content()
    }
}

/**
 * 全局导航控制器Provider
 * @param content 组件内容
 */
@Composable
fun ProviderNavController(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavController provides rememberAnimatedNavController()) {
        content()
    }
}

/**
 * 全局滚动阴影效果隐藏Provider android 28以上适用
 * @param content 组件内容
 */
@Composable
fun ProviderOverScroll(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        content()
    }
}

/**
 * 全局屏幕适配Provider
 * @param width 适配设计稿宽度
 * @param content 组件内容
 */
@Composable
fun ProviderDensity(width: Float = 360f, content: @Composable () -> Unit) {
    val fontScale = LocalDensity.current.fontScale
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val sizePixels =
        if (LocalContext.current.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            displayMetrics.widthPixels else displayMetrics.heightPixels

    CompositionLocalProvider(
        LocalDensity provides Density(density = sizePixels / width, fontScale = fontScale)
    ) {
        content()
    }
}