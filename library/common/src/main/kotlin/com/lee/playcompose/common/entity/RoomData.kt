package com.lee.playcompose.common.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author jv.lee
 * @date 2020/4/16
 * @description 数据库操作类
 */

@Keep
@Entity
data class SearchHistory(
    @ColumnInfo(name = "search_history_key") @PrimaryKey(autoGenerate = false) val key: String,
    @ColumnInfo(name = "search_history_time") val time: Long = System.currentTimeMillis()
)
