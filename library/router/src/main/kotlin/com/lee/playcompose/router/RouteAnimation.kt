/*
 * compose navigation animation code.
 * @author jv.lee
 * @date 2022/7/11
 */
package com.lee.playcompose.router

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

private const val animationDuration = 300

fun enterSlideIn() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(animationDuration)
)

fun exitSlideIn() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(animationDuration)
)

fun popEnterSlideIn() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(animationDuration)
)

fun popExitSlideIn() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(animationDuration)
)

fun exitSlideInOut() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> (-(fullWidth * 0.1)).toInt() },
    animationSpec = tween(animationDuration)
)

fun popEnterSlideInOut() = slideInHorizontally(
    initialOffsetX = { fullWidth -> (-(fullWidth * 0.1)).toInt() },
    animationSpec = tween(animationDuration)
)

@OptIn(ExperimentalAnimationApi::class)
fun enterZoom() = scaleIn(initialScale = 0.95f, animationSpec = tween(animationDuration))

@OptIn(ExperimentalAnimationApi::class)
fun exitZoom() = scaleOut(targetScale = 1.05f, animationSpec = tween(animationDuration))

@OptIn(ExperimentalAnimationApi::class)
fun popEnterZoom() = scaleIn(initialScale = 1.05f, animationSpec = tween(animationDuration))

@OptIn(ExperimentalAnimationApi::class)
fun popExitAlphaHide() = fadeOut(targetAlpha = 1f, animationSpec = tween(animationDuration))