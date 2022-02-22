package com.lee.playcompose.common.extensions

import androidx.compose.animation.*
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

/**
 * @author jv.lee
 * @date 2022/2/21
 * @description
 */

@Composable
@ExperimentalAnimationApi
fun SimpleAnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    route: String? = null,
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition) =
        { fadeIn(initialAlpha = 0F) },
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition) =
        { fadeOut(targetAlpha = 0F) },
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition) = enterTransition,
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition) = exitTransition,
    builder: NavGraphBuilder.() -> Unit
) {
    AnimatedNavHost(
        navController,
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier,
        contentAlignment,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition
    )
}

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
                ), targetOffsetX = { -(it * 0.25).toInt() })
        },
        // 关闭页面进入动画
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -(it * 0.25).toInt() })
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