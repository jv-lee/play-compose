package com.lee.playcompose.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.R
import com.lee.playcompose.base.core.ApplicationExtensions.app
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/28
 * @description
 */
class SplashViewModel : ViewModel() {

    var viewStates by mutableStateOf(SplashViewState())
        private set

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
        viewStates = viewStates.copy(splashVisible = false, contentVisible = true)
    }

}

data class SplashViewState(
    val splashVisible: Boolean = true,
    val contentVisible: Boolean = false,
    val timeVisible: Boolean = false,
    val timeText: String = "",
)

sealed class SplashViewAction {
    object StartTimeTask : SplashViewAction()
    object NavigationMain : SplashViewAction()
}