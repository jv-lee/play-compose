package com.lee.playcompose.ui.page

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.R
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.home.HomePage
import com.lee.playcompose.me.MePage
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.square.SquarePage
import com.lee.playcompose.system.SystemPage

/**
 * @author jv.lee
 * @date 2022/2/18
 * @description
 */
@ExperimentalAnimationApi
@Composable
fun MainPage(navController: NavHostController) {
    val selectIndex = remember { mutableStateOf(0) }
    val navigationInsets =
        rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)

    Scaffold(backgroundColor = AppTheme.colors.window, bottomBar = {
        BottomNavigation(backgroundColor = AppTheme.colors.item, elevation = 3.dp) {
            tabItems.forEachIndexed { index, item ->
                val isSelect = selectIndex.value == index
                BottomNavigationItem(selected = isSelect, icon = {
                    NavigationIcon(isSelected = isSelect, item = item)
                }, onClick = {
                    selectIndex.value = index
                })
            }
        }
    }, content = {
        Box(
            modifier = Modifier.padding(
                bottom = navigationInsets.calculateBottomPadding()
            )
        ) {
            when (selectIndex.value) {
                0 -> HomePage(navController)
                1 -> SquarePage(navController)
                2 -> SystemPage(navController)
                3 -> MePage(navController)
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

private val tabItems = listOf(MainTab.Home, MainTab.Square, MainTab.System, MainTab.Me)

private sealed class MainTab(val name: String, val icon: Int, val selectIcon: Int) {
    object Home : MainTab("Home", R.drawable.vector_home, R.drawable.vector_home_fill)
    object Square : MainTab("Square", R.drawable.vector_square, R.drawable.vector_square_fill)
    object System : MainTab("System", R.drawable.vector_system, R.drawable.vector_system_fill)
    object Me : MainTab("Me", R.drawable.vector_me, R.drawable.vector_me_fill)
}

