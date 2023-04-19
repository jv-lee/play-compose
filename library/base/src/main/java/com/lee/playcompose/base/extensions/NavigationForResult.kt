/*
 * compose Navigation 获取页面回传数据封装
 * @author jv.lee
 * @date 2022/4/11
 */
package com.lee.playcompose.base.extensions

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val NAVIGATION_FOR_RESULT_VERSION = "version"
const val NAVIGATION_FOR_RESULT_DATA = "data"

@Composable
inline fun <reified T> NavController.forResult(
    key: String,
    delayTimeMillis: Long = 0,
    crossinline callback: (T?) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    var forResultVersion by remember { mutableStateOf(System.currentTimeMillis()) }

    currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(key)
        ?.observeAsState()?.value?.let {
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

fun <T> NavController.setResult(key: String, data: T) {
    val bundle = Bundle()
    bundle.putLong(NAVIGATION_FOR_RESULT_VERSION, System.currentTimeMillis())
    bundle.put(NAVIGATION_FOR_RESULT_DATA, data)
    previousBackStackEntry?.savedStateHandle?.set(key, bundle)
}

@Composable
inline fun NavController.forResultBundle(
    key: String,
    delayTimeMillis: Long = 0,
    crossinline callback: (Bundle?) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    var forResultVersion by remember { mutableStateOf(System.currentTimeMillis()) }

    currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(key)
        ?.observeAsState()?.value?.let {
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

fun NavController.setBundleResult(key: String, bundle: Bundle) {
    val savedBundle = Bundle()
    savedBundle.putLong(NAVIGATION_FOR_RESULT_VERSION, System.currentTimeMillis())
    savedBundle.putAll(bundle)
    previousBackStackEntry?.savedStateHandle?.set(key, savedBundle)
}

fun NavController.setResult(key: String) {
    setResult(key, 0)
}
