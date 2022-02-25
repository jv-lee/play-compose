package com.lee.playcompose

import com.lee.playcompose.base.core.BaseApplication
import com.lee.playcompose.base.tools.DarkModeTools

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
class App : BaseApplication() {
    override fun init() {
        DarkModeTools.get(this)
    }

    override fun unInit() {

    }
}