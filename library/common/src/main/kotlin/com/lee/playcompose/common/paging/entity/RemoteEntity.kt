/*
 * paging分页数据 实体类
 * @author jv.lee
 * @date 2022/4/13
 */
package com.lee.playcompose.common.paging.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 远程数据本地Room实体
 * @param id 数据id
 * @param remoteKey 数据key
 * @param content 实际内容json字符串
 */
@Keep
@Entity
data class RemoteContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var remoteKey: String = "",
    val content: String
)

/**
 * 远程数据本地存储key Room实体
 * @param remoteKey 数据key
 * @param nextKey 下一页数据key
 */
@Keep
@Entity
data class RemoteKey(
    @PrimaryKey
    val remoteKey: String,
    val nextKey: Int?
)