package com.lee.playcompose.todo.ui.callback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author jv.lee
 * @date 2022/4/7
 * @description todoList页面自页面通信回调
 */
interface TodoListCallback {
    fun refresh()
}

class TodoListCallbackHandler(lifecycle: Lifecycle) {
    private val callbacks = arrayListOf<TodoListCallback>()

    fun addCallback(callback: TodoListCallback) {
        callbacks.add(callback)
    }

    fun dispatchCallback() {
        callbacks.forEach { it.refresh() }
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
fun rememberCallbackHandler(lifecycle: Lifecycle) = remember {
    mutableStateOf(TodoListCallbackHandler(lifecycle = lifecycle))
}