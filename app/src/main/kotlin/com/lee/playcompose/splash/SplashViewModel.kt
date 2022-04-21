package com.lee.playcompose.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.R
import com.lee.playcompose.base.core.ApplicationExtensions.app
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/28
 * @description
 */
class SplashViewModel : ViewModel() {

    var viewStates by mutableStateOf(SplashViewState())
        private set

    private val _viewEvents = Channel<SplashViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: SplashViewAction) {
        when (action) {
            is SplashViewAction.NavigationMain -> {
                navigationMain()
            }
            is SplashViewAction.StartTimeTask -> {
                playTime()
            }
        }
    }

    private fun playTime() {
        viewModelScope.launch {
            flowOf(5, 4, 3, 2, 1)
                .onStart { viewStates = viewStates.copy(timeVisible = true) }
                .onCompletion { navigationMain() }
                .collect {
                    val timeText = app.getString(R.string.splash_time_text, it)
                    viewStates = viewStates.copy(timeText = timeText)
                    delay(1000)
                }
        }
    }

    private fun navigationMain() {
        viewStates = viewStates.copy(contentVisible = true)
//        viewModelScope.launch {
//            _viewEvents.send(SplashViewEvent.NavigationMain)
//        }
    }

}

data class SplashViewState(
    val contentVisible: Boolean = false,
    val timeText: String = "",
    val timeVisible: Boolean = false
)

sealed class SplashViewEvent {
    object NavigationMain : SplashViewEvent()
}

sealed class SplashViewAction {
    object StartTimeTask : SplashViewAction()
    object NavigationMain : SplashViewAction()
}