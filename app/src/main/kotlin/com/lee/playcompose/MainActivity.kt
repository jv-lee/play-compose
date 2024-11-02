package com.lee.playcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.base.ktx.ProviderDensity
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
        super.onCreate(savedInstanceState)
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
