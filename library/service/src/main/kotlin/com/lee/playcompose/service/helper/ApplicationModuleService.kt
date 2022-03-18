package com.lee.playcompose.service.helper

import android.app.Application
import com.lee.playcompose.service.ApplicationService

import java.util.*

/**
 * @author jv.lee
 * @date 2021/9/9
 * @description 各模块application初始化回调
 */
object ApplicationModuleService {
    fun init(application: Application) {
        val iterator = ServiceLoader.load(ApplicationService::class.java).iterator()
        while (iterator.hasNext()) {
            iterator.next().init(application)
        }
    }
}