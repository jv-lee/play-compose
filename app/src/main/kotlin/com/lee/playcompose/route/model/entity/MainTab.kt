package com.lee.playcompose.route.model.entity

import com.lee.playcompose.R
import com.lee.playcompose.router.RoutePage

/**
 * 主页tab数据实体
 * @author jv.lee
 * @date 2022/7/14
 */
sealed class MainTab(val route: String, val icon: Int, val selectIcon: Int) {
    object Home : MainTab(RoutePage.Home.route, R.drawable.vector_home, R.drawable.vector_home_fill)
    object Square :
        MainTab(RoutePage.Square.route, R.drawable.vector_square, R.drawable.vector_square_fill)

    object System :
        MainTab(RoutePage.System.route, R.drawable.vector_system, R.drawable.vector_system_fill)

    object Me : MainTab(RoutePage.Me.route, R.drawable.vector_me, R.drawable.vector_me_fill)

    companion object {
        fun getMainTabs() = listOf(Home, Square, System, Me)
    }
}