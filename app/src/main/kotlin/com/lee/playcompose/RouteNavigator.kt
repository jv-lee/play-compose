package com.lee.playcompose

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.widget.RouteBackHandler
import com.lee.playcompose.details.DetailsPage
import com.lee.playcompose.home.ui.page.HomePage
import com.lee.playcompose.me.MePage
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.square.SquarePage
import com.lee.playcompose.system.SystemPage

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
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
            CheckNavigation(currentDestination?.route) {
                BottomNavigation(backgroundColor = AppTheme.colors.item, elevation = 3.dp) {
                    tabItems.forEachIndexed { _, item ->
                        val isSelect =
                            currentDestination?.hierarchy?.any { it.route == item.name } == true

                        BottomNavigationItem(selected = isSelect, icon = {
                            NavigationIcon(isSelected = isSelect, item = item)
                        }, onClick = {
                            navController.navigate(item.name) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                    }
                }
            }
        },
        content = {
            NavHost(
                modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
                navController = navController,
                startDestination = MainTab.Home.name
            ) {
                composable(PageRoute.Home.route) { HomePage(navController = navController) }
                composable(PageRoute.Square.route) { SquarePage(navController = navController) }
                composable(PageRoute.System.route) { SystemPage(navController = navController) }
                composable(PageRoute.Me.route) { MePage(navController = navController) }
                composable(PageRoute.Details.route) { DetailsPage(navController = navController) }
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
private fun CheckNavigation(route: String? = null, content: @Composable () -> Unit = {}) {
    when (route) {
        PageRoute.Home.route, PageRoute.Square.route, PageRoute.System.route, PageRoute.Me.route -> {
            content()
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