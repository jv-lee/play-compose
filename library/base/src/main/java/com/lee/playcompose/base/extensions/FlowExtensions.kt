/*
 * Flow扩展函数帮助类
 * @author jv.lee
 * @date 2022/8/11
 */
package com.lee.playcompose.base.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.delay(timeMillis: Long) = map {
    kotlinx.coroutines.delay(timeMillis)
    it
}
