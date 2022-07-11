/*
 * app全局路由扩展函数
 * @author jv.lee
 * @date 2022/3/7
 */
package com.lee.playcompose.router

import android.net.Uri
import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.lee.playcompose.base.net.HttpManager

private fun enterSlide() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300)
)

private fun exitSlide() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300)
)

private fun popEnterSlide() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300)
)

private fun popExitSlide() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300)
)

@ExperimentalAnimationApi
fun NavGraphBuilder.tabComposable(
    route: String,
    tabRoutes: List<String> = emptyList(),
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route, arguments, deepLinks, content = content,
        // 打开页面退出动画
        exitTransition = {
            if (tabRoutes.contains(this.targetState.destination.route)) {
                null
            } else {
                exitSlide()
            }
        },
        // 关闭页面进入动画
        popEnterTransition = {
            if (tabRoutes.contains(this.initialState.destination.route)) {
                null
            } else {
                popEnterSlide()
            }
        }
    )
}

@ExperimentalAnimationApi
fun NavGraphBuilder.sideComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route, arguments, deepLinks, content = content,
        // 打开页面进入动画
        enterTransition = { enterSlide() },
        // 打开页面退出动画
        exitTransition = { exitSlide() },
        // 关闭页面进入动画
        popEnterTransition = { popEnterSlide() },
        // 关闭页面退出动画
        popExitTransition = { popExitSlide() },
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
        list.add(navArgument(it.key) {
            type = checkType(it.value)
        })
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