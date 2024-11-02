package com.lee.playcompose.startup

import android.content.Context
import androidx.startup.Initializer
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.common.BuildConfig
import com.lee.playcompose.common.ktx.setCommonInterceptor
import com.lee.playcompose.common.paging.db.RemoteCacheDatabase
import com.lee.playcompose.service.helper.ApplicationModuleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * 项目自动初始化 进行模块、工具类、基础配置初始化
 * @author jv.lee
 * @date 2023/2/7
 */
class AutoInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // 初始化深色模式工具
            DarkModeTools.init(context)
            // 缓存管理工具初始化
            CacheManager.init(context, BuildConfig.VERSION_CODE)
            // 初始化网络拦截器
            HttpManager.instance.setCommonInterceptor()
            // 初始化远程缓存数据库
            RemoteCacheDatabase.getInstance(context)
            // 子模块统一初始化
            ApplicationModuleService.init(context)
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = Collections.emptyList()
}