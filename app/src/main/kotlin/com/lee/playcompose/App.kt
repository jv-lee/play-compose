package com.lee.playcompose

import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.core.BaseApplication
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.DarkModeTools
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
        }
    }

    override fun unInit() {}
}