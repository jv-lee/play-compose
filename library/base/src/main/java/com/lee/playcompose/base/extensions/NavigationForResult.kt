/*
 * compose Navigation 获取页面回传数据封装
 * @author jv.lee
 * @date 2022/4/11
 */
package com.lee.playcompose.base.extensions

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val NAVIGATION_FOR_RESULT_VERSION = "version"
const val NAVIGATION_FOR_RESULT_DATA = "data"

/**
 * 接收页面返回的数据-适用于单数据返回
 * @param key 回传数据key
 * @param delayTimeMillis 回传数据接收延时毫秒
 * @param callback 回传数据回调接口，参数为回传数据
 */
@Composable
inline fun <reified T> NavController.forResult(
    key: String,
    delayTimeMillis: Long = 0,
    crossinline callback: (T?) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    var forResultVersion by remember { mutableLongStateOf(System.currentTimeMillis()) }

    currentBackStackEntry?.savedStateHandle?.getStateFlow<Bundle?>(key, null)
        ?.collectAsState()?.value?.let {
            val version = it.getLong(NAVIGATION_FOR_RESULT_VERSION)
            val data = it.getValueOrNull<T>(NAVIGATION_FOR_RESULT_DATA)
            if (forResultVersion != version) {
                forResultVersion = version
                coroutine.launch {
                    delay(delayTimeMillis)
                    callback(data)
                }
            }
        }
}

/**
 * 设置页面回退返回的数据-适用于单数据返回
 * @param key 回传数据key
 * @param data 回传的数据
 */
fun <T> NavController.setResult(key: String, data: T) {
    val bundle = Bundle()
    bundle.putLong(NAVIGATION_FOR_RESULT_VERSION, System.currentTimeMillis())
    bundle.put(NAVIGATION_FOR_RESULT_DATA, data)
    previousBackStackEntry?.savedStateHandle?.set(key, bundle)
}

/**
 * 接收页面返回的数据-适用于多数据返回
 * @param key 回传数据key
 * @param delayTimeMillis 回传数据接收延时毫秒
 * @param callback 回传数据回调接口，参数为回传Bundle数据
 */
@Composable
inline fun NavController.ForResultBundle(
    key: String,
    delayTimeMillis: Long = 0,
    crossinline callback: (Bundle?) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    var forResultVersion by remember { mutableLongStateOf(System.currentTimeMillis()) }

    currentBackStackEntry?.savedStateHandle?.getStateFlow<Bundle?>(key, null)
        ?.collectAsState()?.value?.let {
            val version = it.getLong(NAVIGATION_FOR_RESULT_VERSION)
            val data = it.getBundle(NAVIGATION_FOR_RESULT_DATA)
            if (forResultVersion != version) {
                forResultVersion = version
                coroutine.launch {
                    delay(delayTimeMillis)
                    callback(data)
                }
            }
        }
}

/**
 * 设置页面回退返回的数据-适用于多数据返回
 * @param key 回传数据key
 * @param bundle 回传的数据bundle
 */
fun NavController.setBundleResult(key: String, bundle: Bundle) {
    val savedBundle = Bundle()
    savedBundle.putLong(NAVIGATION_FOR_RESULT_VERSION, System.currentTimeMillis())
    savedBundle.putAll(bundle)
    previousBackStackEntry?.savedStateHandle?.set(key, savedBundle)
}

/**
 * 设置空数据返回只做回调处理
 * 数据接收页面只监听返回响应处理，不接收数据
 * @param key 监听返回key
 */
fun NavController.setResult(key: String) {
    setResult(key, 0)
}
