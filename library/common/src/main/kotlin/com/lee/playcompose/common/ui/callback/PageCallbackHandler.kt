package com.lee.playcompose.common.ui.callback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * todoList页面自页面通信回调
 * @author jv.lee
 * @date 2022/4/7
 */
class PageCallbackHandler<T>(lifecycle: Lifecycle) {
    private val callbacks = hashMapOf<String, T>()

    fun addCallback(key: String, callback: T) {
        callbacks[key] = callback
    }

    fun removeCallback(key: String, callback: T) {
        callbacks.remove(key)
    }

    fun notifyAll(interfaceCall: (T) -> Unit) {
        callbacks.forEach {
            interfaceCall(it.value)
        }
    }

    fun notifyAt(key: String, interfaceCall: (T) -> Unit) {
        callbacks[key]?.run(interfaceCall)
    }

    init {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    callbacks.clear()
                }
            }
        })
    }
}

@Composable
fun <T> rememberPageCallbackHandler(lifecycle: LifecycleOwner) = remember {
    mutableStateOf(PageCallbackHandler<T>(lifecycle = lifecycle.lifecycle))
}