package com.lee.playcompose.route

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lee.playcompose.BuildConfig
import com.lee.playcompose.R
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.bus.ChannelBus.Companion.post
import com.lee.playcompose.common.entity.LoginEvent
import com.lee.playcompose.common.entity.NavigationSelectEvent
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.FloatingBox
import com.lee.playcompose.common.ui.widget.ReindexType
import com.lee.playcompose.common.ui.widget.SimpleAnimatedNavHost
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.system.ui.theme.NavigationTabHeight
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description app路由管理
 */
@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
@Composable
fun Activity.RouteNavigator() {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 绑定导航选中事件:通知选中页面数据列表移动至顶部
    LaunchedEffect(Unit) {
        ChannelBus.bindChannel<NavigationSelectEvent>(lifecycle)
    }

    // 绑定登陆事件:导航至登陆页
    LaunchedEffect(Unit) {
        ChannelBus.bindChannel<LoginEvent>(lifecycle)?.receiveAsFlow()?.collect {
            toast(resources.getString(R.string.login_token_failed))
            navController.navigateArgs(RoutePage.Account.Login.route)
        }
    }

    Scaffold(
        modifier = Modifier.padding(rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)),
        backgroundColor = AppTheme.colors.background,
        bottomBar = {
            CheckNavigation(currentDestination?.route) { hasClick ->
                BottomNavigation(backgroundColor = AppTheme.colors.item, elevation = 3.dp) {
                    tabItems.forEachIndexed { _, item ->
                        val isSelect =
                            currentDestination?.hierarchy?.any { it.route == item.route } == true

                        BottomNavigationItem(selected = isSelect, icon = {
                            NavigationIcon(isSelected = isSelect, item = item)
                        }, onClick = {
                            bottomItemNavigation(hasClick, item.route, navController)
                            ChannelBus.getChannel<NavigationSelectEvent>()?.post(
                                NavigationSelectEvent(item.route)
                            )
                        })
                    }
                }
            }
        },
        content = { paddingValues ->
            SimpleAnimatedNavHost(
                navController = navController,
                startDestination = MainTab.Home.route
            ) {
                appRouteManifest(this, navController, paddingValues)
            }
        })

    // 添加全局FloatingView
    FloatingView()
}

@Composable
private fun NavigationIcon(isSelected: Boolean, item: MainTab) {
    val icon = if (isSelected) item.selectIcon else item.icon
    val color = if (isSelected) AppTheme.colors.focus else AppTheme.colors.primary
    Icon(painterResource(id = icon), null, tint = color)
}

@Composable
private fun CheckNavigation(route: String? = null, content: @Composable (Boolean) -> Unit = {}) {
    val visible = when (route) {
        RoutePage.Home.route, RoutePage.Square.route, RoutePage.System.route, RoutePage.Me.route -> {
            true
        }
        else -> false
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing)
        )
    ) {
        content(visible)
    }
    // 设置占位容器防止离开主页时bottomNavigation平移后内容空间多出间距拉伸滑动导致底部显示移动
    if (!visible) Box(modifier = Modifier.height(NavigationTabHeight))
}

/**
 * bottomItem 导航
 */
private fun bottomItemNavigation(hasClick: Boolean, route: String, navController: NavController) {
    if (hasClick) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

private val tabItems = listOf(MainTab.Home, MainTab.Square, MainTab.System, MainTab.Me)

private sealed class MainTab(val route: String, val icon: Int, val selectIcon: Int) {
    object Home : MainTab(RoutePage.Home.route, R.drawable.vector_home, R.drawable.vector_home_fill)
    object Square :
        MainTab(RoutePage.Square.route, R.drawable.vector_square, R.drawable.vector_square_fill)

    object System :
        MainTab(RoutePage.System.route, R.drawable.vector_system, R.drawable.vector_system_fill)

    object Me : MainTab(RoutePage.Me.route, R.drawable.vector_me, R.drawable.vector_me_fill)
}

@Composable
private fun FloatingView() {
    if (BuildConfig.DEBUG) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars))
                .padding(bottom = NavigationTabHeight),
        ) {
            FloatingBox(
                type = ReindexType.REINDEX_X,
                shape = CircleShape,
                modifier = Modifier
                    .size(68.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(26.dp))
                    .align(alignment = Alignment.BottomEnd)
            ) {
                Image(
                    painter = painterResource(id = com.lee.playcompose.common.R.mipmap.ic_launcher_round),
                    contentDescription = null,
                    modifier = Modifier.clickable { toast("Welcome to Play Compose.") }
                )
            }
        }
    }
}