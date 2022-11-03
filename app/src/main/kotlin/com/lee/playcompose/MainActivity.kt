package com.lee.playcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.base.extensions.ProviderDensity
import com.lee.playcompose.common.ui.theme.PlayComposeTheme
import com.lee.playcompose.route.RouteNavigator
import com.lee.playcompose.splash.SplashLauncher

/**
 * 程序主窗口 单Activity架构
 * @author jv.lee
 * @date 2022/2/24
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 沉浸式状态栏
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            PlayComposeTheme {
                SplashLauncher {
                    ProviderDensity {
                        RouteNavigator()
                    }
                }
            }
        }
    }
}
