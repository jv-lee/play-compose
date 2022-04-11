package com.lee.playcompose.base.extensions

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * @author jv.lee
 * @date 2022/4/11
 * @description
 */
@Parcelize
@Keep
data class NavigationForResult(val version: String, val code: Int) : Parcelable

@Composable
fun NavController.forResult(key: String, callback: (Int) -> Unit) {
    val coroutine = rememberCoroutineScope()
    var forResultVersion by remember { mutableStateOf(System.currentTimeMillis().toString()) }

    currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationForResult>(key)
        ?.observeAsState()?.value?.let {
            if (forResultVersion != it.version) {
                forResultVersion = it.version
                coroutine.launch {
                    delay(500)
                    callback(it.code)
                }
            }
        }
}

fun NavController.setResult(key: String, code: Int) {
    previousBackStackEntry?.savedStateHandle?.set(
        key, NavigationForResult(System.currentTimeMillis().toString(), code)
    )
}