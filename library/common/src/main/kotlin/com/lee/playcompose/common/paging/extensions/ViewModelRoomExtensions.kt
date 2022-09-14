/*
 * paging3分页数据加载使用room数据库做缓存处理
 * @author jv.lee
 * @date 2022/4/13
 */
@file:OptIn(ExperimentalPagingApi::class)

package com.lee.playcompose.common.paging.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.room.withTransaction
import com.google.gson.reflect.TypeToken
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.paging.db.RemoteCacheDatabase
import com.lee.playcompose.common.paging.entity.RemoteContent
import com.lee.playcompose.common.paging.entity.RemoteKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * paging3结合room数据库缓存分页数据创建
 * @param config 分页配置
 * @param initialKey 首页请求key
 * @param savedKey 缓存key
 * @param requestAction 分页数据网络请求suspend函数
 */
inline fun <reified T : Any> ViewModel.roomPager(
    config: PagingConfig = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        initialLoadSize = 20,
        enablePlaceholders = true
    ),
    initialKey: Int = 0,
    savedKey: String = this.javaClass.simpleName,
    noinline requestAction: suspend (page: Int) -> PageData<T>
): Flow<PagingData<T>> {
    val database = RemoteCacheDatabase.get()
    return Pager(
        config = config,
        initialKey = initialKey,
        remoteMediator = RemoteRoomMediator(
            savedKey,
            initialKey,
            database,
            requestAction,
        )
    ) {
        database.remoteContentDao().getList(remoteKey = savedKey)
    }.flow.map { pagingData ->
        pagingData.map { remoteContent ->
            val type = object : TypeToken<T>() {}.type
            HttpManager.getGson().fromJson<T>(remoteContent.content, type)
        }
    }.cachedIn(viewModelScope)
}

/**
 * paging3结合room数据库缓存分页数据创建 {单页面数据}
 * @param config 分页配置
 * @param savedKey 缓存key
 * @param requestAction 分页数据网络请求suspend函数
 */
inline fun <reified T : Any> ViewModel.singleRoomPager(
    config: PagingConfig = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        initialLoadSize = 20,
        enablePlaceholders = true
    ),
    initialKey: Int = 0,
    savedKey: String = this.javaClass.simpleName,
    noinline requestAction: suspend (page: Int) -> List<T>
): Flow<PagingData<T>> {
    val database = RemoteCacheDatabase.get()
    return Pager(
        config = config,
        initialKey = initialKey,
        remoteMediator = RemoteRoomMediator(
            savedKey,
            initialKey,
            database,
        ) { page ->
            PageData(data = requestAction(page))
        }
    ) {
        database.remoteContentDao().getList(remoteKey = savedKey)
    }.flow.map { pagingData ->
        pagingData.map { remoteContent ->
            val type = object : TypeToken<T>() {}.type
            HttpManager.getGson().fromJson<T>(remoteContent.content, type)
        }
    }.cachedIn(viewModelScope)
}

class RemoteRoomMediator<T>(
    private val savedKey: String,
    private val initialKey: Int,
    private val database: RemoteCacheDatabase,
    private val requestAction: suspend (page: Int) -> PageData<T>,
) : RemoteMediator<Int, RemoteContent>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RemoteContent>
    ): MediatorResult {
        return when (loadType) {
            // 首次访问 || PagingDataAdapter.refresh()
            LoadType.REFRESH -> {
                loadRefresh(loadType, state)
            }
            // 刷新数据到位后设置当前数据成功状态显示
            LoadType.PREPEND -> {
                loadPrepend(loadType, state)
            }
            // 加载更多分页
            LoadType.APPEND -> {
                loadAppend(loadType, state)
            }
        }
    }

    private suspend fun loadRefresh(
        loadType: LoadType,
        state: PagingState<Int, RemoteContent>
    ): MediatorResult {
        return loadDataTransaction(loadType, initialKey)
    }

    private fun loadPrepend(
        loadType: LoadType,
        state: PagingState<Int, RemoteContent>
    ): MediatorResult {
        return MediatorResult.Success(endOfPaginationReached = true)
    }

    private suspend fun loadAppend(
        loadType: LoadType,
        state: PagingState<Int, RemoteContent>
    ): MediatorResult {
        val page = database.withTransaction {
            database.remoteKeyDao().getRemoteKey(remoteKey = savedKey)
        }?.nextKey ?: run {
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        return loadDataTransaction(loadType, page)
    }

    private suspend fun loadDataTransaction(
        loadType: LoadType,
        page: Int,
    ): MediatorResult {
        try {
            val result = requestAction(page)
            val endOfPaginationReached = result.curPage >= result.pageCount

            val item = result.data.map {
                RemoteContent(
                    remoteKey = savedKey,
                    content = HttpManager.getGson().toJson(it)
                )
            }

            // 插入数据库
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().clearRemoteKey(savedKey)
                    database.remoteContentDao().clear(savedKey)
                }
                val nextKey = if (endOfPaginationReached) null else page + 1
                val entity = RemoteKey(remoteKey = savedKey, nextKey = nextKey)

                database.remoteKeyDao().insert(entity)
                database.remoteContentDao().insertList(item)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            // 错误情况处理
            return loadFailed(loadType, e)
        }
    }

    private suspend fun loadFailed(loadType: LoadType, e: Exception): MediatorResult {
        return database.withTransaction {
            val count = database.remoteContentDao().getCount()
            when (loadType) {
                LoadType.REFRESH, LoadType.PREPEND -> {
                    if (count > 0) {
                        MediatorResult.Success(endOfPaginationReached = false)
                    } else {
                        MediatorResult.Error(e)
                    }
                }
                LoadType.APPEND -> {
                    MediatorResult.Error(e)
                }
            }
        }
    }

}