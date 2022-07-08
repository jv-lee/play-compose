package com.lee.playcompose.route

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.BuildConfig
import com.lee.playcompose.R
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.bus.ChannelBus.Companion.post
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.entity.LoginEvent
import com.lee.playcompose.common.entity.NavigationSelectEvent
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.FloatingBox
import com.lee.playcompose.common.ui.widget.ReindexType
import com.lee.playcompose.common.ui.widget.SimpleAnimatedNavHost
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.system.ui.theme.NavigationTabHeight
import kotlinx.coroutines.flow.receiveAsFlow
import com.lee.playcompose.common.R as CR
import com.lee.playcompose.home.R as HR

/**
 * app路由管理
 * @author jv.lee
 * @date 2022/2/24
 */
@Composable
fun Activity.RouteNavigator() {
    val activity = LocalActivity.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 绑定登陆事件:导航至登陆页
    LaunchedEffect(Unit) {
        ChannelBus.bindChannel<LoginEvent>(lifecycle)?.receiveAsFlow()?.collect {
            toast(resources.getString(R.string.login_token_failed))
            ModuleService.find<AccountService>().requestLogout(activity)
            navController.navigateArgs(RoutePage.Account.Login.route)
        }
    }

    Box(
        modifier = Modifier
            .padding(rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars))
            .background(AppTheme.colors.background)
    ) {
        // 内容路由
        SimpleAnimatedNavHost(
            navController = navController,
            startDestination = MainTab.Home.route,
            builder = { appRouteManifest() })

        // 首页导航navigationBar
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
    }

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
private fun BoxScope.CheckNavigation(
    route: String? = null,
    content: @Composable (Boolean) -> Unit = {}
) {
    val visible = when (route) {
        RoutePage.Home.route, RoutePage.Square.route, RoutePage.System.route, RoutePage.Me.route -> {
            true
        }
        else -> false
    }
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter),
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
                    .size(56.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(26.dp))
                    .align(alignment = Alignment.BottomEnd)
            ) {
                Image(
                    painter = painterResource(id = CR.mipmap.ic_launcher_round),
                    contentDescription = null,
                    modifier = Modifier.clickable { toast(app.getString(HR.string.home_header_text)) }
                )
            }
        }
    }
}