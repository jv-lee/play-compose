package com.lee.playcompose.base.extensions

import com.google.gson.reflect.TypeToken
import com.lee.playcompose.base.cache.CacheManager

/**
 * @author jv.lee
 * @date 2021/9/9
 * @description 缓存管理器扩展类
 */

/**
 * @param key 缓存key
 */
inline fun <reified T> CacheManager.getCache(key: String): T? {
    val type = object : TypeToken<T>() {}.type
    return get<T>(key, type)
}

/**
 * @param key 存储key
 * @param data 存储数据源
 */
inline fun <reified T> CacheManager.putCache(key: String, data: T) {
    put(key, data)
}

/**
 * 清除缓存
 * @param key 缓存key
 */
fun CacheManager.clearCache(key: String) {
    put(key, "")
}

