package com.lee.playcompose.search.model.db

import androidx.room.Dao
import androidx.room.Query
import com.lee.playcompose.base.db.base.BaseDao
import com.lee.playcompose.common.entity.SearchHistory

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
@Dao
interface SearchHistoryDao : BaseDao<SearchHistory> {

    /**
     * 查询最近搜索倒序5条数据
     */
    @Query("SELECT * FROM SearchHistory ORDER BY search_history_time DESC LIMIT 0,5")
    fun querySearchHistory(): List<SearchHistory>

    /**
     * 清楚所有搜索记录
     */
    @Query("DELETE FROM SearchHistory WHERE 1=1")
    fun clearSearchHistory()

}