package com.lee.playcompose

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lee.playcompose.common.extensions.RouteBackHandler
import com.lee.playcompose.common.extensions.SimpleAnimatedNavHost
import com.lee.playcompose.common.extensions.sideComposable
import com.lee.playcompose.details.DetailsPage
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.ui.page.MainPage
import com.lee.playcompose.ui.page.SplashPage

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@ExperimentalAnimationApi
@Composable
fun Activity.RouteNavigator() {
    val navController = rememberAnimatedNavController()
    RouteBackHandler({ finish() }, navController, PageRoute.Main.route)
    SplashScreen {
        SimpleAnimatedNavHost(
            modifier = Modifier.navigationBarsPadding(),
            navController = navController,
            startDestination = PageRoute.Main.route,
        ) {
            sideComposable(PageRoute.Main.route) { MainPage(navController) }
            sideComposable(PageRoute.Details.route) { DetailsPage(navController) }
        }
    }
}

@Composable
fun SplashScreen(content: @Composable () -> Unit) {
    var isSplash by remember { mutableStateOf(true) }
    if (isSplash) {
        SplashPage { isSplash = false }
    } else {
        content()
    }
}