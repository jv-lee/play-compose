package com.lee.playcompose.common.paging.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * paging数据库存储分页remote缓存实体
 * @author jv.lee
 * @date 2022/4/13
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