package com.lee.playcompose.service.core

import android.content.Context

/**
 * 各模块application初始化接口
 * @author jv.lee
 * @date 2021/9/9
 */
interface ApplicationService : IModuleService {
    fun init(context: Context)
}