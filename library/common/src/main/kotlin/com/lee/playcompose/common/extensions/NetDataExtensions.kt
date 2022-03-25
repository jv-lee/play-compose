package com.lee.playcompose.common.extensions

import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.constants.ApiConstants.REQUEST_TOKEN_ERROR_MESSAGE
import com.lee.playcompose.common.entity.Data

/**
 * @author jv.lee
 * @date 2021/11/25
 * @description
 */
fun <T> Data<T>.checkData(): T {
    if (errorCode == ApiConstants.REQUEST_OK) {
        return data
    } else if (errorCode == ApiConstants.REQUEST_TOKEN_ERROR) {
        throw RuntimeException(REQUEST_TOKEN_ERROR_MESSAGE)
    }
    throw RuntimeException(errorMsg)
}