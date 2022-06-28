/*
 * liveDataBus 事件通知实体
 * @author jv.lee
 * @date 2021/11/17
 */
package com.lee.playcompose.common.entity

import androidx.annotation.Keep

/**
 * 主页导航tab选中事件
 * @param route 选中tab的路由
 */
@Keep
data class NavigationSelectEvent(val route: String)

/**
 * 账号登陆事件
 */
@Keep
class LoginEvent

/**
 * 主页显示事件
 */
@Keep
class ContentVisibleEvent