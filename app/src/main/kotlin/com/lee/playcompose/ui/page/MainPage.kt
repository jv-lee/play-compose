package com.lee.playcompose.ui.page

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lee.playcompose.R
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.home.HomePage
import com.lee.playcompose.me.MePage
import com.lee.playcompose.square.SquarePage
import com.lee.playcompose.system.SystemPage

/**
 * @author jv.lee
 * @date 2022/2/18
 * @description
 */
@ExperimentalAnimationApi
@Composable
fun MainPage(rootNavController: NavHostController) {
    val selectIndex = remember { mutableStateOf(0) }

    Scaffold(Modifier.fillMaxSize(), backgroundColor = AppTheme.colors.window, bottomBar = {
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
    },
        content = {
            when (selectIndex.value) {
                0 -> HomePage(navController = rootNavController)
                1 -> SquarePage(navController = rootNavController)
                2 -> SystemPage(navController = rootNavController)
                3 -> MePage(navController = rootNavController)
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

