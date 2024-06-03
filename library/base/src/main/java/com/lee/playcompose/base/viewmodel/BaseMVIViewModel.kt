@file:Suppress("LeakingThis", "PropertyName")

package com.lee.playcompose.base.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface IViewState // View状态数据 订阅后可多次消费
interface IViewEvent // View执行事件 订阅后只在激活状态下消费一次，非激活状态发送不消费
interface IViewIntent // View主动执行意图 - 通过意图驱动ui状态数据变化

/**
 * ViewModel基类 - MVI模式
 *
 * [ViewEvent] 页面单次事件消费，订阅后处理如Toast、Dialog、导航等单次执行事件
 * [ViewIntent] 页面主动调用意图，意图驱动Ui更新逻辑
 *
 * @author jv.lee
 * @date 2024/5/31
 */
abstract class BaseMVIViewModel<ViewEvent : IViewEvent, ViewIntent : IViewIntent> : ViewModel() {

    /**
     * 页面动作单次事件 - 页面事件单次消费
     */
    protected val _viewEvents: Channel<ViewEvent> = Channel(Channel.BUFFERED)
    val viewEvents: Flow<ViewEvent> = _viewEvents.receiveAsFlow()

    /**
     * 主动触发事件，ui界面通过事件流触发请求更新逻辑
     * 该方法提供给view层调用
     * @param intent 具体执行事件
     */
    abstract fun dispatch(intent: ViewIntent)
}