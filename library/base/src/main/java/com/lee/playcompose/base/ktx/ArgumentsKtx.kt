package com.lee.playcompose.base.ktx

/*
 * 获取bundle参数扩展函数
 * @author jv.lee
 * @date 2020/8/19
 */

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

fun <T> Bundle.put(key: String, values: T) {
    when (values) {
        is String -> {
            putString(key, values)
        }
        is Int -> {
            putInt(key, values)
        }
        is Long -> {
            putLong(key, values)
        }
        is Boolean -> {
            putBoolean(key, values)
        }
        is Float -> {
            putFloat(key, values)
        }
        is java.io.Serializable -> {
            putSerializable(key, values)
        }
        is Parcelable -> {
            putParcelable(key, values)
        }
    }
}

inline fun <reified T> Bundle.getValueOrNull(key: String): T? {
    return when (T::class.java) {
        java.lang.Integer::class.java -> getInt(key, 0) as T
        java.lang.Long::class.java -> getLong(key, 0L) as T
        java.lang.String::class.java -> getString(key, "") as T
        java.lang.Boolean::class.java -> getBoolean(key, false) as T
        java.lang.Float::class.java -> getFloat(key, 0f) as T

        else -> {
            when {
                T::class.java.interfaces.contains(java.io.Serializable::class.java) -> {
                    getSerializableCompat(key)
                }
                T::class.java.interfaces.contains(android.os.Parcelable::class.java) -> {
                    getParcelableCompat(key)
                }
                else -> {
                    null
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : java.io.Serializable> Bundle.getSerializableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key) as? T
    }
}