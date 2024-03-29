package com.lee.playcompose.base.core

import android.app.Application
import androidx.multidex.MultiDexApplication

/**
 * 项目基础baseApplication类
 * @author jv.lee
 * @date 2020/3/20
 */
abstract class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ApplicationExtensions.app = this
        init()
    }

    override fun onTerminate() {
        super.onTerminate()
        unInit()
    }

    protected abstract fun init()
    protected abstract fun unInit()
}

object ApplicationExtensions {
    lateinit var app: Application
}