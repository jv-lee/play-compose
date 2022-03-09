package com.lee.playcompose

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.lee.playcompose.base.core.BaseApplication
import com.lee.playcompose.base.interadp.SimpleActivityLifecycleCallbacks
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.base.tools.StatusTools.setDarkStatusIcon
import com.lee.playcompose.base.tools.StatusTools.setLightStatusIcon
import com.lee.playcompose.base.tools.StatusTools.setNavigationBarColor
import com.lee.playcompose.common.extensions.setCommonInterceptor

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 程序主入口
 * 进行模块、工具类、基础配置初始化 Activity生命周期统一监听功能业务处理
 */
class App : BaseApplication() {

    private val activityLifecycleCallbacks = object : SimpleActivityLifecycleCallbacks() {

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            // 夜间模式主题适配
            if (DarkModeTools.get().isDarkTheme()) {
                activity.setNavigationBarColor(Color.BLACK)
                activity.setLightStatusIcon()
            } else {
                activity.setNavigationBarColor(Color.WHITE)
                activity.setDarkStatusIcon()
            }
            super.onActivityCreated(activity, bundle)
        }

    }

    override fun init() {
        DarkModeTools.get(this)
        // 初始化网络拦截器
        HttpManager.getInstance().setCommonInterceptor()
        // 注册Activity生命周期监听
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    override fun unInit() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}