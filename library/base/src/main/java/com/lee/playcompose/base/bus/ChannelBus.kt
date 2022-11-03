@file:Suppress("UNCHECKED_CAST")

package com.lee.playcompose.base.bus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * compose channel事件总线
 * @author jv.lee
 * @date 2022/3/22
 */
class ChannelBus {

    companion object {
        val bus: ConcurrentHashMap<String, Channel<*>> = ConcurrentHashMap()

        inline fun <reified T> bindChannel(lifecycle: Lifecycle): Channel<T>? {
            val target = T::class.java.name
            lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        lifecycle.removeObserver(this)
                        bus.remove(target)
                    }
                }
            })
            return getChannel()
        }

        inline fun <reified T> getChannel(): Channel<T>? {
            val target = T::class.java.name
            if (!bus.containsKey(target)) {
                bus[target] = Channel<T>(Channel.BUFFERED)
            }
            return bus[target] as? Channel<T>
        }

        inline fun <reified T> Channel<T>.post(item: T) {
            CoroutineScope(Dispatchers.IO).launch { send(item) }
        }
    }
}