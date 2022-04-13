package com.lee.playcompose.common.paging

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author jv.lee
 * @date 2022/4/13
 * @description
 */
@Keep
@Entity
data class RemoteContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var remoteKey: String = "",
    val content: String
)

@Keep
@Entity
data class RemoteKey(
    @PrimaryKey
    val remoteKey: String,
    val nextKey: Int?
)