package com.lee.playcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.base.extensions.ProviderDensity
import com.lee.playcompose.common.extensions.agentWebPreload
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
        agentWebPreload()
        setContent {
            PlayComposeTheme { // 设置应用主题
                SplashLauncher { // 设置应用首屏闪屏启动器
                    ProviderDensity { // 设置屏幕适配
                        RouteNavigator() // 设置主路由组件控制页面显示跳转
                    }
                }
            }
        }
    }
}
