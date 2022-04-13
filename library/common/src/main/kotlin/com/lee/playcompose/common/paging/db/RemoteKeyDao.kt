package com.lee.playcompose.common.paging.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lee.playcompose.common.paging.RemoteKey

/**
 * @author jv.lee
 * @date 2022/4/13
 * @description
 */
@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKeyEntity: RemoteKey)

    @Query("SELECT * FROM RemoteKey where remoteKey = :remoteKey ")
    suspend fun getRemoteKey(remoteKey: String): RemoteKey?

    @Query("DELETE FROM RemoteKey where remoteKey = :remoteKey")
    suspend fun clearRemoteKey(remoteKey: String)
}