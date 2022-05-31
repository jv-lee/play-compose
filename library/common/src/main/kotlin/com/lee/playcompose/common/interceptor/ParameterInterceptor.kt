package com.lee.playcompose.common.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 公共参数拦截器
 * @author jv.lee
 * @date 2021/11/24
 */
class ParameterInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newUrl = request.url().newBuilder()
            .addQueryParameter("commonType", "")
            .build()

        val newRequest = request.newBuilder().url(newUrl).build()

        return chain.proceed(newRequest)
    }
}