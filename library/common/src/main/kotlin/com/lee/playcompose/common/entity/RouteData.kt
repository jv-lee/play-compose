/*
 * 全局路由实体
 * @author jv.lee
 * @date 2022/3/8
 */
package com.lee.playcompose.common.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * 内容详情页面跳转透传数据实体
 * @param id 文章id
 * @param title 文章title
 * @param link 文章是否被点赞
 * @param isCollect 文章是否被收藏
 */
@Parcelize
@Keep
data class DetailsData(
    val id: String = EMPTY_ID,
    val title: String,
    val link: String,
    var isCollect: Boolean = false
) : Parcelable {
    companion object {
        const val EMPTY_ID = "0"
    }
}