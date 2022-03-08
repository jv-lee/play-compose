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
data class DetailsData(val title: String, val link: String) : Parcelable