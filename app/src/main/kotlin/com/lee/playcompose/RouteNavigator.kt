package com.lee.playcompose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lee.playcompose.common.R
import com.lee.playcompose.common.extensions.SimpleAnimatedNavHost
import com.lee.playcompose.common.extensions.sideComposable
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.ui.page.MainPage

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@ExperimentalAnimationApi
@Composable
fun RouteNavigator(backCallback: () -> Unit) {
    val navController = rememberAnimatedNavController()
    RouteBackHandler(backCallback, navController)
    Scaffold(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding(), content = {
        SimpleAnimatedNavHost(
            navController = navController,
            startDestination = PageRoute.Main.route,
        ) {
            sideComposable(PageRoute.Main.route) { MainPage(navController) }
        }
    })
}

@Composable
private fun RouteBackHandler(backCallback: () -> Unit, navController: NavController) {
    var firstTime: Long = 0
    val message = stringResource(id = R.string.back_alert_message)
    BackHandler(enabled = true) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == PageRoute.Main.route) {
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