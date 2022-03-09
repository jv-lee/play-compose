package com.lee.playcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.lee.playcompose.common.ui.theme.PlayComposeTheme
import com.lee.playcompose.navigator.RouteNavigator
import com.lee.playcompose.navigator.SplashNavigator

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 程序主窗口 单Activity架构
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            PlayComposeTheme {
                SplashNavigator {
                    RouteNavigator()
                }
            }
        }
    }

}
