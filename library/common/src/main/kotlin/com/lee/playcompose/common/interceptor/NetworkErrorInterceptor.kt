package com.lee.playcompose.common.interceptor

import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.bus.ChannelBus.Companion.post
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.utils.NetworkUtil
import com.lee.playcompose.common.entity.NetworkErrorEvent
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 网络是否可用拦截器
 * @author jv.lee
 * @date 2021/11/24
 */
class NetworkErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!NetworkUtil.isConnected(app)) {
            ChannelBus.getChannel<NetworkErrorEvent>()?.post(NetworkErrorEvent())
        }

        return chain.proceed(request)
    }
}