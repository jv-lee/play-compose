package com.lee.playcompose.common.interceptor

import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.constants.ApiConstants
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Cookie处理拦截器 用于保存账号cookie信息
 * @author jv.lee
 * @date 2021/11/24
 */
class SaveCookieInterceptor : Interceptor {

    companion object {
        private const val HEADER_COOKIE_KEY = "Cookie" // 请求头设置cookie键名
        private const val SET_COOKIE_KEY = "set-cookie" // 包含cookie的response取值键名
        private const val CONTAINER_COOKIE_URI = "lg/" // 链接中包含lg则需要设置cookie
        private const val SAVE_TOKEN_KEY = "save-token" // 保存cookie 键名

        private const val CONTAINER_LOGIN_URI = "user/login"
        private const val CONTAINER_REGISTER_URI = "user/register"
        private const val CONTAINER_LOGOUT_URI = "user/logout"
    }

    var cookie: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val url = request.url().toString()


        // request请求设置cookie
        if (url.contains(ApiConstants.BASE_URI) && url.contains(CONTAINER_COOKIE_URI)) {
            if (cookie.isEmpty()) {
                cookie = PreferencesTools.get(SAVE_TOKEN_KEY)
            }
            builder.addHeader(HEADER_COOKIE_KEY, cookie)
        }

        val response = chain.proceed(builder.build())

        //登陆注册时保存登陆 cookie作为token校验接口header参数
        if ((url.contains(CONTAINER_LOGIN_URI) || url.contains(CONTAINER_REGISTER_URI))
            && response.headers(SET_COOKIE_KEY).isNotEmpty()
        ) {
            val cookies = response.headers(SET_COOKIE_KEY)
            val cookie = encodeCookie(cookies)
            saveCookie(cookie)
        } else if (url.contains(CONTAINER_LOGOUT_URI)) {
            clearCookie()
        }

        return response
    }

    private fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set = HashSet<String>()
        cookies
            .map { cookie ->
                cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
            .forEach { array ->
                array.filterNot { set.contains(it) }.forEach { set.add(it) }
            }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }

    private fun saveCookie(cookie: String) {
        this.cookie = cookie
        PreferencesTools.put(SAVE_TOKEN_KEY, cookie)
    }

    private fun clearCookie() {
        this.cookie = ""
        PreferencesTools.remove(SAVE_TOKEN_KEY)
    }

}