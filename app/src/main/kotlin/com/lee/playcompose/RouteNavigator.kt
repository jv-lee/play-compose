package com.lee.playcompose

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.RouteBackHandler
import com.lee.playcompose.common.ui.widget.SimpleAnimatedNavHost
import com.lee.playcompose.details.DetailsPage
import com.lee.playcompose.home.ui.page.HomePage
import com.lee.playcompose.me.MePage
import com.lee.playcompose.router.*
import com.lee.playcompose.square.SquarePage
import com.lee.playcompose.system.SystemPage

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description app路由管理\页面路由注册
 */
@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
@Composable
fun Activity.RouteNavigator() {
    val navigationInsets =
        rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)

    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // double click close app.
    RouteBackHandler({ finish() }, navController, mainRoutes)

    Scaffold(
        modifier = Modifier.padding(bottom = navigationInsets.calculateBottomPadding()),
        backgroundColor = AppTheme.colors.background,
        bottomBar = {
            CheckNavigation(currentDestination?.route) { hasClick ->
                BottomNavigation(backgroundColor = AppTheme.colors.item, elevation = 3.dp) {
                    tabItems.forEachIndexed { _, item ->
                        val isSelect =
                            currentDestination?.hierarchy?.any { it.route == item.name } == true

                        BottomNavigationItem(selected = isSelect, icon = {
                            NavigationIcon(isSelected = isSelect, item = item)
                        }, onClick = {
                            bottomItemNavigation(hasClick, item.name, navController)
                        })
                    }
                }
            }
        },
        content = { paddingValues ->
            SimpleAnimatedNavHost(
                navController = navController,
                startDestination = MainTab.Home.name
            ) {
                composable(PageRoute.Home.route) {
                    HomePage(navController = navController, paddingValues)
                }
                composable(PageRoute.Square.route) {
                    SquarePage(navController = navController, paddingValues)
                }
                composable(PageRoute.System.route) {
                    SystemPage(navController = navController, paddingValues)
                }
                composable(PageRoute.Me.route) {
                    MePage(navController = navController, paddingValues)
                }
                sideComposable(
                    PageRoute.Details.parseRoute(),
                    arguments = PageRoute.Details.parseArguments()
                ) { entry ->
                    val gson = HttpManager.getGson()
                    val detailsDataJson = entry.arguments?.getString(ParamsKey.detailsDataKey)
                    val detailsData = gson.fromJson(detailsDataJson, DetailsData::class.java)
                    DetailsPage(navController = navController, detailsData)
                }
            }
        })
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
        PageRoute.Home.route, PageRoute.Square.route, PageRoute.System.route, PageRoute.Me.route -> {
            true
        }
        else -> false
    }
    AnimatedVisibility(
        visible = visible, enter = slideInVertically(
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

private val mainRoutes =
    listOf(PageRoute.Home.route, PageRoute.Square.route, PageRoute.System.route, PageRoute.Me.route)

private val tabItems = listOf(MainTab.Home, MainTab.Square, MainTab.System, MainTab.Me)

private sealed class MainTab(val name: String, val icon: Int, val selectIcon: Int) {
    object Home : MainTab("Home", R.drawable.vector_home, R.drawable.vector_home_fill)
    object Square : MainTab("Square", R.drawable.vector_square, R.drawable.vector_square_fill)
    object System : MainTab("System", R.drawable.vector_system, R.drawable.vector_system_fill)
    object Me : MainTab("Me", R.drawable.vector_me, R.drawable.vector_me_fill)
}