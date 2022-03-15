package com.lee.playcompose.router

import android.net.Uri
import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.lee.playcompose.base.net.HttpManager

/**
 * @author jv.lee
 * @date 2022/3/7
 * @description 路由扩展函数
 */
@ExperimentalAnimationApi
fun NavGraphBuilder.sideComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(route = route, arguments, deepLinks, content = content,
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

fun NavController.navigateArgs(route: String, vararg args: Any) {
    val argumentsBuilder = StringBuilder()
    args.iterator()
        .forEach { arg ->
            argumentsBuilder.append(checkTypeFormat(arg))
        }
    navigate(route + argumentsBuilder.toString())
}

fun PageRoute.parseRoute(): String {
    val builder = StringBuilder(route)
    paramsKey.forEach {
        builder.append("/{${it.key}}")
    }
    return builder.toString()
}

fun PageRoute.parseArguments(): List<NamedNavArgument> {
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