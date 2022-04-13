package com.lee.playcompose.common.paging.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lee.playcompose.common.paging.RemoteContent

/**
 * @author jv.lee
 * @date 2022/4/13
 * @description
 */
@Dao
interface RemoteContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(contentList: List<RemoteContent>)

    @Query("SELECT * FROM RemoteContent where remoteKey = :remoteKey")
    fun getList(remoteKey: String): PagingSource<Int, RemoteContent>

    @Query("DELETE FROM RemoteContent where remoteKey = :remoteKey")
    suspend fun clear(remoteKey: String)

    @Query("SELECT COUNT(*) FROM RemoteContent")
    fun getCount(): Int
}