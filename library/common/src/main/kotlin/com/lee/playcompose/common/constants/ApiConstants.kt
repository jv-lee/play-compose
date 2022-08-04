package com.lee.playcompose.common.constants

import com.lee.playcompose.common.BuildConfig

/**
 * 全局通用api常量
 * @author jv.lee
 * @date 2021/12/3
 */
object ApiConstants {
    // baseApi
    const val BASE_URI = BuildConfig.BASE_URI

    // 站点api请求 成功码
    const val REQUEST_OK = 0

    // 未登陆 错误码
    const val REQUEST_TOKEN_ERROR = -1001

    // 登陆token失效错误自负
    const val REQUEST_TOKEN_ERROR_MESSAGE = "TOKEN-ERROR"

    // 链接中包含lg则需要设置cookie
    const val API_CONTAINER_COOKIE = "lg/"

    // 积分规则地址
    const val URI_COIN_HELP = "https://www.wanandroid.com/blog/show/2653"
}