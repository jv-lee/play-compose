package com.lee.playcompose

import android.app.Activity
import android.os.Bundle
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.core.BaseApplication
import com.lee.playcompose.base.interadp.SimpleActivityLifecycleCallbacks
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.common.extensions.appThemeSet
import com.lee.playcompose.common.extensions.runInternalBlock
import com.lee.playcompose.common.extensions.setCommonInterceptor
import com.lee.playcompose.common.paging.db.RemoteCacheDatabase
import com.lee.playcompose.service.helper.ApplicationModuleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 程序主入口
 * 进行模块、工具类、基础配置初始化 Activity生命周期统一监听功能业务处理
 * @author jv.lee
 * @date 2022/2/24
 */
class App : BaseApplication() {

    private val activityLifecycleCallbacks = object : SimpleActivityLifecycleCallbacks() {

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            // 应用内夜间模式主题适配
            activity.runInternalBlock { activity.appThemeSet() }
            super.onActivityCreated(activity, bundle)
        }

    }

    override fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            // 初始化深色模式工具
            DarkModeTools.init(applicationContext)
            // 缓存管理工具初始化
            CacheManager.init(applicationContext, BuildConfig.VERSION_CODE)
            // 初始化网络拦截器
            HttpManager.instance.setCommonInterceptor()
            // 初始化远程缓存数据库
            RemoteCacheDatabase.getInstance(applicationContext)
            // 子模块统一初始化
            ApplicationModuleService.init(this@App)

            // 注册Activity生命周期监听
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        }
    }

    override fun unInit() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}