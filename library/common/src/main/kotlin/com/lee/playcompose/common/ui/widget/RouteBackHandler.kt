package com.lee.playcompose.common.ui.widget

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalActivity
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ktx.toast

/**
 * 拦截back事件 双击回退处理
 * @author jv.lee
 * @date 2022/3/1
 */
@Composable
fun RouteBackHandler() {
    val activity = LocalActivity.current
    var firstTime: Long = 0
    val message = stringResource(id = R.string.back_alert_message)
    BackHandler(enabled = true) {
        val secondTime = System.currentTimeMillis()
        // 如果两次按键时间间隔大于2秒，则不退出
        if (secondTime - firstTime > 2000) {
            toast(message)
            // 更新firstTime
            firstTime = secondTime
        } else {
            // 两次按键小于2秒时，回调back事件
            activity.finish()
        }
    }
}

@Composable
fun RouteBackHandler(
    backCallback: () -> Unit,
    navController: NavController,
    mainRoutes: List<String>
) {
    var firstTime: Long = 0
    val message = stringResource(id = R.string.back_alert_message)
    BackHandler(enabled = true) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (mainRoutes.contains(currentRoute)) {
            val secondTime = System.currentTimeMillis()
            // 如果两次按键时间间隔大于2秒，则不退出
            if (secondTime - firstTime > 2000) {
                toast(message)
                // 更新firstTime
                firstTime = secondTime
            } else {
                // 两次按键小于2秒时，回调back事件
                backCallback()
            }
        } else {
            navController.popBackStack()
        }
    }
}
