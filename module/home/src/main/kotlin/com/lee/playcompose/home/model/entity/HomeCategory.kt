package com.lee.playcompose.home.model.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.lee.playcompose.home.R
import com.lee.playcompose.router.RoutePage

/**
 * 首页分类本地数据帮助类
 * @author jv.lee
 * @date 2021/11/4
 */
data class HomeCategory(
    @DrawableRes val iconResId: Int,
    @StringRes val nameResId: Int,
    val route: String
) {
    companion object {
        /**
         * 提供本地首页分类数据
         */
        fun getHomeCategory() = arrayListOf(
            HomeCategory(
                R.drawable.vector_icon_official,
                R.string.home_category_official,
                RoutePage.Official.route
            ),
            HomeCategory(
                R.drawable.vector_icon_project,
                R.string.home_category_project,
                RoutePage.Project.route
            )
        )
    }
}