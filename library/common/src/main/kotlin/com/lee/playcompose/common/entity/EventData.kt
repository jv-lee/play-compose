package com.lee.playcompose.common.entity

import androidx.annotation.Keep

/**
 * @author jv.lee
 * @date 2021/11/17
 * @description
 */
@Keep
data class NavigationSelectEvent(val route: String)

@Keep
class LoginEvent

@Keep
class RegisterSuccessEvent

@Keep
class CreateShareSuccessEvent

@Keep
class NavigationForResult<T>(val version: Int = 0, data: T) {
    private val versions = arrayListOf<Int>()

    companion object {
        fun <T> NavigationForResult<T>.change(change:(data: T)->Unit) {

        }
    }
}