package com.lee.playcompose.splash

import androidx.lifecycle.viewModelScope
import com.lee.playcompose.R
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.bus.ChannelBus.Companion.post
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.ContentVisibleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import com.lee.playcompose.common.R as CR

/**
 * 闪屏页viewModel
 * @author jv.lee
 * @date 2022/3/28
 */
class SplashViewModel : BaseMVIViewModel<SplashViewState, IViewEvent, SplashViewIntent>() {

    init {
        initSplashInfo()
    }

    override fun initViewState() = SplashViewState()

    override fun dispatch(intent: SplashViewIntent) {
        when (intent) {
            is SplashViewIntent.RequestSplashAd -> {
                playTime()
            }

            is SplashViewIntent.HideSplash -> {
                hideSplash()
            }

            is SplashViewIntent.ShowContent -> {
                showContent()
            }
        }
    }

    private fun playTime() {
        viewModelScope.launch {
            flowOf(5, 4, 3, 2, 1)
                .onStart { _viewStates = _viewStates.copy(splashAdVisible = true) }
                .onCompletion { hideSplash() }
                .collect {
                    val timeText = app.getString(R.string.splash_time_text, it)
                    _viewStates = _viewStates.copy(timeText = timeText)
                    delay(1000)
                }
        }
    }

    private fun showContent() {
        _viewStates = _viewStates.copy(contentVisible = true)
    }

    private fun hideSplash() {
        _viewStates = _viewStates.copy(splashVisible = false)

        // splash隐藏后发送全局事件 主页内容显示通知提供给ui显示后一些处理
        ChannelBus.getChannel<ContentVisibleEvent>()?.post(ContentVisibleEvent())
    }

    private fun initSplashInfo() {
        val splashInfoRes = if (DarkModeTools.get().isDarkTheme()) {
            CR.mipmap.ic_splash_info_night
        } else CR.mipmap.ic_splash_info
        _viewStates = _viewStates.copy(splashInfoRes = splashInfoRes)
    }
}

data class SplashViewState(
    val splashVisible: Boolean = true,
    val contentVisible: Boolean = false,
    val splashAdVisible: Boolean = false,
    val timeText: String = "",
    val splashLogoRes: Int = CR.mipmap.ic_splash_logo,
    val splashInfoRes: Int = CR.mipmap.ic_splash_info
) : IViewState

sealed class SplashViewIntent : IViewIntent {
    data object RequestSplashAd : SplashViewIntent()
    data object ShowContent : SplashViewIntent()
    data object HideSplash : SplashViewIntent()
}