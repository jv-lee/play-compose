package com.lee.playcompose.route

import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.route.model.entity.MainTab
import com.lee.playcompose.router.RoutePage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * App主页路由viewModel
 * @author jv.lee
 * @date 2022/7/12
 */
class RouteNavigatorViewModel :
    BaseMVIViewModel<RouteNavigationViewState, RouteNavigationViewEvent, RouteNavigationViewIntent>() {
    override fun initViewState(): RouteNavigationViewState = RouteNavigationViewState()

    override fun dispatch(intent: RouteNavigationViewIntent) {
        when (intent) {
            is RouteNavigationViewIntent.NavigationItemClick -> {
                onNavigationItem(intent)
            }

            is RouteNavigationViewIntent.NetworkError -> {
                networkErrorVisible()
            }
        }
    }


    private fun onNavigationItem(intent: RouteNavigationViewIntent.NavigationItemClick) {
        if (!intent.hasClick) return

        viewModelScope.launch {
            // 单次点击触发导航到目标页面
            _viewEvents.send(
                RouteNavigationViewEvent.OnNavigationItem(intent.route)
            )

            // 双击navigationItem 触发列表滚动至顶部
            _viewStates.run {
                val currentTimeMillis = System.currentTimeMillis()
                if (currentRoute == intent.route && (currentTimeMillis - clickTimeMillis) < 200) {
                    _viewEvents.send(RouteNavigationViewEvent.OnDoubleClickNavigationItem(intent.route))
                }
                _viewStates = _viewStates.copy(
                    currentRoute = intent.route, clickTimeMillis = currentTimeMillis
                )
            }
        }
    }

    private fun networkErrorVisible() {
        if (_viewStates.networkVisible) return
        viewModelScope.launch {
            _viewStates = _viewStates.copy(networkVisible = true)
            delay(3000)
            _viewStates = _viewStates.copy(networkVisible = false)
        }
    }

}

data class RouteNavigationViewState(
    val tabItems: List<MainTab> = MainTab.getMainTabs(),
    val networkVisible: Boolean = false,
    val currentRoute: String = RoutePage.Home.route,
    val clickTimeMillis: Long = System.currentTimeMillis()
) : IViewState

sealed class RouteNavigationViewEvent : IViewEvent {
    data class OnNavigationItem(val route: String) : RouteNavigationViewEvent()

    data class OnDoubleClickNavigationItem(val route: String) : RouteNavigationViewEvent()
}

sealed class RouteNavigationViewIntent : IViewIntent {
    data class NavigationItemClick(val hasClick: Boolean, val route: String) :
        RouteNavigationViewIntent()

    data object NetworkError : RouteNavigationViewIntent()

}