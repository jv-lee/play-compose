@file:Suppress("UNCHECKED_CAST")
package com.lee.playcompose.base.tools

import java.lang.ref.WeakReference

/**
 * 弱引用数据暂存帮助类，解除intent中无法携带过大数据时通过该帮助类中转数据
 * @author jv.lee
 * @date 2022/3/14
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
     * @param key
     * @return data
     */
    fun <T> getData(key: String): T? {
        val weakReference: WeakReference<T?> = dataMap[key] as WeakReference<T?>
        return weakReference.get()
    }
}