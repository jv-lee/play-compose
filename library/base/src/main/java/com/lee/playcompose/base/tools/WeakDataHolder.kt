package com.lee.playcompose.base.tools

import java.lang.ref.WeakReference

/**
 * @author jv.lee
 * @date 2022/3/14
 * @description
 */
class WeakDataHolder {
    companion object {
        val instance by lazy { WeakDataHolder() }
    }

    private val dataMap = hashMapOf<String, Any>()

    /**
     * 数据存储
     * @param key
     * @param data
     */
    fun <T> saveData(key: String, data: T?) {
        dataMap[key] = WeakReference(data)
    }

    /**
     * 获取数据
     * @param id
     * @return data
     */
    fun <T> getData(key: String): T? {
        val weakReference: WeakReference<T?> = dataMap[key] as WeakReference<T?>
        return weakReference.get()
    }
}