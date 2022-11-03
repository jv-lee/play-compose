package com.lee.playcompose.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.R
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.bus.ChannelBus.Companion.post
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.tools.DarkModeTools
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
class SplashViewModel : ViewModel() {

    var viewStates by mutableStateOf(SplashViewState())
        private set

    init {
        initSplashInfo()
    }

    fun dispatch(action: SplashViewAction) {
        when (action) {
            is SplashViewAction.RequestSplashAd -> {
                playTime()
            }
            is SplashViewAction.HideSplash -> {
                hideSplash()
            }
            is SplashViewAction.ShowContent -> {
                showContent()
            }
        }
    }

    private fun playTime() {
        viewModelScope.launch {
            flowOf(5, 4, 3, 2, 1)
                .onStart { viewStates = viewStates.copy(splashAdVisible = true) }
                .onCompletion { hideSplash() }
                .collect {
                    val timeText = app.getString(R.string.splash_time_text, it)
                    viewStates = viewStates.copy(timeText = timeText)
                    delay(1000)
                }
        }
    }

    private fun showContent() {
        viewStates = viewStates.copy(contentVisible = true)
    }

    private fun hideSplash() {
        viewStates = viewStates.copy(splashVisible = false)

        // splash隐藏后发送全局事件 主页内容显示通知提供给ui显示后一些处理
        ChannelBus.getChannel<ContentVisibleEvent>()?.post(ContentVisibleEvent())
    }

    private fun initSplashInfo() {
        val splashInfoRes = if (DarkModeTools.get().isDarkTheme()) {
            CR.mipmap.ic_splash_info_night
        } else CR.mipmap.ic_splash_info
        viewStates = viewStates.copy(splashInfoRes = splashInfoRes)
    }
}

data class SplashViewState(
    val splashVisible: Boolean = true,
    val contentVisible: Boolean = false,
    val splashAdVisible: Boolean = false,
    val timeText: String = "",
    val splashLogoRes: Int = CR.mipmap.ic_splash_logo,
    val splashInfoRes: Int = CR.mipmap.ic_splash_info
)

sealed class SplashViewAction {
    object RequestSplashAd : SplashViewAction()
    object ShowContent : SplashViewAction()
    object HideSplash : SplashViewAction()
}