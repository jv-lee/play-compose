package com.lee.playcompose.me.model.entity

import com.lee.playcompose.common.R
import com.lee.playcompose.router.RoutePage

/**
 * 我的页面item 数据实体
 * @author jv.lee
 * @date 2022/7/14
 */
sealed class MeItem(
    val route: String,
    val name: Int,
    val icon: Int,
    val arrow: Int = R.drawable.vector_arrow
) {
    object Coin : MeItem(
        RoutePage.Me.Coin.route,
        com.lee.playcompose.me.R.string.me_item_coin,
        com.lee.playcompose.me.R.drawable.vector_coin
    )

    object Collect :
        MeItem(
            RoutePage.Me.Collect.route,
            com.lee.playcompose.me.R.string.me_item_collect,
            com.lee.playcompose.me.R.drawable.vector_collect
        )

    object Share :
        MeItem(
            RoutePage.Square.MyShare.route,
            com.lee.playcompose.me.R.string.me_item_share,
            com.lee.playcompose.me.R.drawable.vector_share
        )

    object TODO : MeItem(
        RoutePage.Todo.route,
        com.lee.playcompose.me.R.string.me_item_todo,
        com.lee.playcompose.me.R.drawable.vector_todo
    )

    object Settings :
        MeItem(
            RoutePage.Me.Settings.route,
            com.lee.playcompose.me.R.string.me_item_settings,
            com.lee.playcompose.me.R.drawable.vector_settings
        )

    companion object {
        fun getMeItems() =
            listOf(Coin, Collect, Share, TODO, Settings)
    }
}