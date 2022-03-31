package com.lee.playcompose.common.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description
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