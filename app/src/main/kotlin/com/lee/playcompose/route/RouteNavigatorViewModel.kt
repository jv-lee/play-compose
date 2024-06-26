package com.lee.playcompose.route

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.route.model.entity.MainTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * App主页路由viewModel
 * @author jv.lee
 * @date 2022/7/12
 */
class RouteNavigatorViewModel : ViewModel() {
    var viewStates by mutableStateOf(RouteNavigationViewState())
        private set

    fun dispatch(intent: RouteNavigationViewIntent) {
        when (intent) {
            is RouteNavigationViewIntent.NetworkError -> {
                networkErrorVisible()
            }
        }
    }

    private fun networkErrorVisible() {
        if (viewStates.networkVisible) return
        viewModelScope.launch {
            viewStates = viewStates.copy(networkVisible = true)
            delay(3000)
            viewStates = viewStates.copy(networkVisible = false)
        }
    }
}

data class RouteNavigationViewState(
    val networkVisible: Boolean = false,
    val tabItems: List<MainTab> = MainTab.getMainTabs()
)

sealed class RouteNavigationViewIntent {
    object NetworkError : RouteNavigationViewIntent()
}