package com.lee.playcompose.search.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lee.playcompose.common.entity.SearchHistory

/**
 * 搜索模块数据库
 * @author jv.lee
 * @date 2021/11/22
 */
@Database(entities = [SearchHistory::class], version = 1, exportSchema = false)
abstract class SearchDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        private const val DBName = "playAndroid-search.db"

        @Volatile
        private var instance: SearchDatabase? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context,
                SearchDatabase::class.java,
                DBName
            )
                .allowMainThreadQueries()
                .build().also { instance = it }
        }

        fun get() =
            instance ?: throw Exception("请先调用SearchHistoryDatabase.getInstance(context) 初始化.")
    }
}