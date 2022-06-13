package com.lee.playcompose.service.core

import android.app.Application
import com.lee.playcompose.service.core.IModuleService

/**
 * 各模块application初始化接口
 * @author jv.lee
 * @date 2021/9/9
 */
interface ApplicationService : IModuleService {
    fun init(application: Application)
}