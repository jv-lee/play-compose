/*
 * app通用扩展
 * @author jv.lee
 * @date 2022/3/7
 */
package com.lee.playcompose.common.extensions

import android.app.Activity
import android.graphics.Color
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.base.tools.StatusTools.setDarkStatusIcon
import com.lee.playcompose.base.tools.StatusTools.setLightStatusIcon
import com.lee.playcompose.base.tools.StatusTools.setNavigationBarColor

/**
 * activity主题根据深色模式适配
 */
fun Activity.appThemeSet() {
    // 主题icon适配
    if (DarkModeTools.get().isDarkTheme()) {
        setNavigationBarColor(Color.BLACK)
        setLightStatusIcon()
    } else {
        setNavigationBarColor(Color.WHITE)
        setDarkStatusIcon()
    }
}

/**
 * 判断当前activity是否为内部activity，过滤第三方库activity不执行 [block] 方法块
 */
fun Activity.runInternalBlock(block: () -> Unit) {
    val appPackageName = packageName.replace(".debug", "")
    if (this::class.java.name.contains(appPackageName)) {
        block()
    }
}