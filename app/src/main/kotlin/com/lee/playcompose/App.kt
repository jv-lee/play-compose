package com.lee.playcompose

import com.lee.playcompose.base.core.BaseApplication
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.common.extensions.setCommonInterceptor

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
class App : BaseApplication() {
    override fun init() {
        DarkModeTools.get(this)
        // 初始化网络拦截器
        HttpManager.getInstance().setCommonInterceptor()
    }

    override fun unInit() {

    }
}