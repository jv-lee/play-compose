package com.lee.playcompose.common.extensions

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

/**
 * @author jv.lee
 * @date 2022/2/21
 * @description
 */
@ExperimentalAnimationApi
fun NavGraphBuilder.sideComposable(
    route: String,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(route = route, content = content,
        // 打开页面进入动画
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it * 2 })
        },
        // 打开页面退出动画
        exitTransition = {
            slideOutHorizontally(
                spring(
                    stiffness = 25F,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                ), targetOffsetX = { -it })
        },
        // 关闭页面进入动画
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it })
        },
        // 关闭页面退出动画
        popExitTransition = {
            slideOutHorizontally(
                spring(
                    stiffness = 50f,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                ), targetOffsetX = { it * 2 })
        }
    )
}

