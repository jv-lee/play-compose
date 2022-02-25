package com.lee.playcompose.common.extensions

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.lee.playcompose.common.R

/**
 * @author jv.lee
 * @date 2022/2/21
 * @description
 */

@Composable
fun RouteBackHandler(backCallback: () -> Unit, navController: NavController, mainRoute: String) {
    var firstTime: Long = 0
    val message = stringResource(id = R.string.back_alert_message)
    BackHandler(enabled = true) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == mainRoute) {
            val secondTime = System.currentTimeMillis()
            //如果两次按键时间间隔大于2秒，则不退出
            if (secondTime - firstTime > 2000) {
                toast(message)
                //更新firstTime
                firstTime = secondTime
            } else {
                //两次按键小于2秒时，回调back事件
                backCallback()
            }

        } else {
            navController.popBackStack()
        }
    }
}

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

