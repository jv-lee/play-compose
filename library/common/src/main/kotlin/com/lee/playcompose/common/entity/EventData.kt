package com.lee.playandroid.library.common.entity

import androidx.annotation.Keep

/**
 * @author jv.lee
 * @date 2021/11/17
 * @description
 */
@Keep
data class NavigationSelectEvent(val title: String) {
    companion object {
        const val key = "NavigationSelectEvent"
    }
}

@Keep
class LoginEvent {
    companion object {
        const val key = "LoginEvent"
    }
}