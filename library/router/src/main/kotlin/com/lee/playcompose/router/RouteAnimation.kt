/*
 * compose navigation animation code.
 * @author jv.lee
 * @date 2022/7/11
 */
package com.lee.playcompose.router

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

fun enterSlideIn() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300)
)

fun exitSlideIn() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300)
)

fun popEnterSlideIn() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300)
)

fun popExitSlideIn() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300)
)

fun exitSlideInOut() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> (-(fullWidth * 0.1)).toInt() },
    animationSpec = tween(300)
)

fun popEnterSlideInOut() = slideInHorizontally(
    initialOffsetX = { fullWidth -> (-(fullWidth * 0.1)).toInt() },
    animationSpec = tween(300)
)