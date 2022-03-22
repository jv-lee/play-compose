package com.lee.playcompose.base.bus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * @author jv.lee
 * @date 2022/3/22
 * @description
 */
class ChannelBus {

    companion object {
        val bus: ConcurrentHashMap<String, Channel<*>> = ConcurrentHashMap()

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