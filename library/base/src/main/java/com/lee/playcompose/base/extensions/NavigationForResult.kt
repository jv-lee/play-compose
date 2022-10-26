/*
 * compose Navigation 获取页面回传数据封装
 * @author jv.lee
 * @date 2022/4/11
 */
package com.lee.playcompose.base.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val NAVIGATION_FRO_RESULT_VERSION = "version"
const val NAVIGATION_FRO_RESULT_DATA = "data"

@Composable
inline fun <reified T> NavController.forResult(
    key: String,
    delay: Long = 0,
    crossinline callback: (T?) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    var forResultVersion by remember { mutableStateOf(System.currentTimeMillis()) }

    currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(key)
        ?.observeAsState()?.value?.let {
            val version = it.getLong(NAVIGATION_FRO_RESULT_VERSION)
            val data = it.getValueOrNull<T>(NAVIGATION_FRO_RESULT_DATA)
            if (forResultVersion != version) {
                forResultVersion = version
                coroutine.launch {
                    delay(delay)
                    callback(data)
                }
            }
        }
}

fun <T> NavController.setResult(key: String, data: T) {
    val bundle = Bundle()
    bundle.putLong(NAVIGATION_FRO_RESULT_VERSION, System.currentTimeMillis())
    bundle.put(NAVIGATION_FRO_RESULT_DATA, data)
    previousBackStackEntry?.savedStateHandle?.set(key, bundle)
}

fun NavController.setResult(key: String) {
    setResult(key, 0)
}

private fun <T> Bundle.put(key: String, values: T) {
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
                    getSerializable(key) as T?
                }
                T::class.java.interfaces.contains(android.os.Parcelable::class.java) -> {
                    getParcelable(key) as T?
                }
                else -> {
                    null
                }
            }
        }
    }
}