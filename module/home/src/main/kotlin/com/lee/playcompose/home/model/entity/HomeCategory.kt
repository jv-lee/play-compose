package com.lee.playcompose.home.model.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.lee.playcompose.home.R
import com.lee.playcompose.router.PageRoute


/**
 * @author jv.lee
 * @date 2021/11/4
 * @description 首页分类本地数据帮助类
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
                PageRoute.Official.route
            ),
            HomeCategory(
                R.drawable.vector_icon_project,
                R.string.home_category_project,
                PageRoute.Project.route
            )
        )
    }
}