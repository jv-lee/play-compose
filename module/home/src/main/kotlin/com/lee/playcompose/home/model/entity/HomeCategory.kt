package com.lee.playcompose.home.model.entity

import com.lee.playcompose.home.R


/**
 * @author jv.lee
 * @date 2021/11/4
 * @description 首页分类本地数据帮助类
 */
data class HomeCategory(val iconResId: Int, val name: String, val link: String) {
    companion object {
        /**
         * 提供本地首页分类数据
         */
        fun getHomeCategory() = arrayListOf(
            HomeCategory(
                R.drawable.vector_icon_official,
                "公众号",
                "play://official"
            ),
            HomeCategory(
                R.drawable.vector_icon_project,
                "项目",
                "play://project"
            )
        )
    }
}