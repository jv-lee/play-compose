package com.lee.playcompose.common.interceptor

import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Cookie处理拦截器 用于保存账号cookie信息
 * @author jv.lee
 * @date 2021/11/24
 */
class SaveCookieInterceptor : Interceptor {

    companion object {
        private const val SET_COOKIE_KEY = "set-cookie"

        private const val SAVE_USER_LOGIN_KEY = "user/login"
        private const val SAVE_USER_REGISTER_KEY = "user/register"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val url = request.url().toString()

        //登陆注册时保存登陆 cookie作为token校验接口header参数
        if ((url.contains(SAVE_USER_LOGIN_KEY) || url.contains(SAVE_USER_REGISTER_KEY))
            && response.headers(SET_COOKIE_KEY).isNotEmpty()
        ) {
            val cookies = response.headers(SET_COOKIE_KEY)
            val cookie = encodeCookie(cookies)
            saveCookie(cookie)
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
        PreferencesTools.put(BuildConfig.BASE_URI, cookie)
    }

}