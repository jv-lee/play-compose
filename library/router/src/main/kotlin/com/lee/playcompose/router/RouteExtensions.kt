/*
 * app全局路由扩展函数
 * @author jv.lee
 * @date 2022/3/7
 */
package com.lee.playcompose.router

import android.net.Uri
import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lee.playcompose.base.net.HttpManager

fun NavGraphBuilder.tabComposable(
    route: String,
    tabDefaultRoutes: List<String> = emptyList(),
    tabZoomRoutes: List<String> = emptyList(),
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content,
        // 打开页面退出动画
        exitTransition = {
            if (tabDefaultRoutes.contains(this.targetState.destination.route())) {
                exitDefault()
            } else if (tabZoomRoutes.contains(this.targetState.destination.route())) {
                exitZoom()
            } else {
                exitSlideInOut()
            }
        },
        // 关闭页面进入动画
        popEnterTransition = {
            if (tabDefaultRoutes.contains(this.initialState.destination.route())) {
                enterDefault()
            } else if (tabZoomRoutes.contains(this.initialState.destination.route())) {
                popEnterZoom()
            } else {
                popEnterSlideInOut()
            }
        }
    )
}

fun NavGraphBuilder.themeComposable(
    route: String,
    tabZoomRoutes: List<String> = emptyList(),
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: EnterTransition = enterSlideIn(),
    exitTransition: ExitTransition = exitSlideInOut(),
    popEnterTransition: EnterTransition = popEnterSlideInOut(),
    popExitTransition: ExitTransition = popExitSlideIn(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content,
        // 打开页面进入动画
        enterTransition = { enterTransition },
        // 关闭页面退出动画
        popExitTransition = { popExitTransition },
        // 打开页面退出动画
        exitTransition = {
            if (tabZoomRoutes.contains(this.targetState.destination.route())) {
                exitZoom()
            } else {
                exitTransition
            }
        },
        // 关闭页面进入动画
        popEnterTransition = {
            if (tabZoomRoutes.contains(this.initialState.destination.route())) {
                popEnterZoom()
            } else {
                popEnterTransition
            }
        }
    )
}

fun NavController.navigateArgs(
    route: String,
    vararg args: Any,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    val argumentsBuilder = StringBuilder()
    args.iterator()
        .forEach { arg ->
            argumentsBuilder.append(checkTypeFormat(arg))
        }

    builder?.let {
        navigate(route + argumentsBuilder.toString(), builder = builder)
    } ?: kotlin.run {
        navigate(route + argumentsBuilder.toString()) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

fun RoutePage.parseRoute(): String {
    val builder = StringBuilder(route)
    paramsKey.forEach {
        builder.append("/{${it.key}}")
    }
    return builder.toString()
}

fun RoutePage.parseArguments(): List<NamedNavArgument> {
    val list = arrayListOf<NamedNavArgument>()
    paramsKey.forEach {
        list.add(
            navArgument(it.key) {
                type = checkType(it.value)
            }
        )
    }
    return list
}

private fun checkType(type: Any): NavType<*> {
    return when (type) {
        is List<*> -> {
            NavType.StringType
        }

        is Parcelable -> {
            NavType.StringType
        }

        is String -> {
            NavType.StringType
        }

        is Int -> {
            NavType.IntType
        }

        is Float -> {
            NavType.FloatType
        }

        is Boolean -> {
            NavType.BoolType
        }

        is Long -> {
            NavType.LongType
        }

        else -> NavType.StringType
    }
}

private fun checkTypeFormat(arg: Any): String {
    return when (arg) {
        is Parcelable -> {
            val json = HttpManager.getGson().toJson(arg)
            val encodeJson = Uri.encode(json)
            String.format("/%s", encodeJson)
        }

        else -> String.format("/%s", arg)
    }
}

private fun NavDestination.route(): String? {
    val index = route?.indexOf("/") ?: -1
    return if (index == -1) {
        route
    } else {
        route?.substring(0, index)
    }
}