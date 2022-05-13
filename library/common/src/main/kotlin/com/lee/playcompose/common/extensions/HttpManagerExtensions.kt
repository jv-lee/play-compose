package com.lee.playcompose.common.extensions

import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.net.request.IRequest
import com.lee.playcompose.base.net.request.Request
import com.lee.playcompose.common.BuildConfig
import com.lee.playcompose.common.interceptor.FailedInterceptor
import com.lee.playcompose.common.interceptor.HeaderInterceptor
import com.lee.playcompose.common.interceptor.ParameterInterceptor
import com.lee.playcompose.common.interceptor.SaveCookieInterceptor
import okhttp3.logging.HttpLoggingInterceptor

/**
 * 设置通用拦截器
 */
fun HttpManager.setCommonInterceptor() {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    putInterceptor(httpLoggingInterceptor)
    putInterceptor(FailedInterceptor())
    putInterceptor(ParameterInterceptor())
    putInterceptor(HeaderInterceptor())
    putInterceptor(SaveCookieInterceptor())
}

inline fun <reified T> createApi(
    baseUri: String = BuildConfig.BASE_URI,
    request: Request = Request(
        baseUri,
        IRequest.ConverterType.JSON,
        callTypes = intArrayOf(IRequest.CallType.COROUTINE, IRequest.CallType.FLOW)
    )
): T {
    return HttpManager.instance.getService(T::class.java, request)
}