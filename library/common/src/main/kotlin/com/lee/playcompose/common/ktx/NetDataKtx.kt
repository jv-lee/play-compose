/*
 * 全局网络数据校验扩展函数
 * @author jv.lee
 * @date 2021/11/25
 */
package com.lee.playcompose.common.ktx

import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.constants.ApiConstants.REQUEST_TOKEN_ERROR_MESSAGE
import com.lee.playcompose.common.entity.Data

/**
 * 校验当前网络数据返回是否成功
 */
fun <T> Data<T>.checkData(): T {
    if (errorCode == ApiConstants.REQUEST_OK) {
        return data
    } else if (errorCode == ApiConstants.REQUEST_TOKEN_ERROR) {
        throw RuntimeException(REQUEST_TOKEN_ERROR_MESSAGE)
    }
    throw RuntimeException(errorMsg)
}