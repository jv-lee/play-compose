package com.lee.playcompose.common.paging.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lee.playcompose.common.paging.entity.RemoteContent
import com.lee.playcompose.common.paging.entity.RemoteKey

/**
 * paging分页数据缓存数据库
 * @author jv.lee
 * @date 2022/4/13
 */
@Database(entities = [RemoteKey::class, RemoteContent::class], version = 1, exportSchema = false)
abstract class RemoteCacheDatabase : RoomDatabase() {

    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun remoteContentDao(): RemoteContentDao

    companion object {
        private const val DBName = "PlayCompose-homePaging.db"

        @Volatile
        private var instance: RemoteCacheDatabase? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, RemoteCacheDatabase::class.java, DBName)
                .allowMainThreadQueries()
                .build().also { instance = it }
        }

        fun get() =
            instance ?: throw Exception("请先调用HomePagingDatabase.getInstance(context) 初始化.")

    }
}