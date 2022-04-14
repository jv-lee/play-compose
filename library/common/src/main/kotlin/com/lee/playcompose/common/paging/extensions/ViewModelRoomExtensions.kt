package com.lee.playcompose.common.paging.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.room.withTransaction
import com.google.gson.reflect.TypeToken
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.common.paging.RemoteContent
import com.lee.playcompose.common.paging.RemoteKey
import com.lee.playcompose.common.paging.db.RemoteCacheDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author jv.lee
 * @date 2022/4/13
 * @description paging3分页数据加载使用room数据库做缓存处理
 */
@OptIn(ExperimentalPagingApi::class)
inline fun <reified T : Any> ViewModel.localPager(
    config: PagingConfig = PagingConfig(20),
    initialKey: Int = 0,
    remoteKey: String = this.javaClass.simpleName,
    isSingle: Boolean = false,
    noinline requestAction: suspend (page: Int) -> List<T>
): Flow<PagingData<T>> {
    val database = RemoteCacheDatabase.get()
    return Pager(
        config = config,
        initialKey = initialKey,
        remoteMediator = RemoteRoomMediator(
            remoteKey,
            initialKey,
            database,
            requestAction,
            isSingle
        )
    ) {
        database.remoteContentDao().getList(remoteKey = remoteKey)
    }.flow.map { pagingData ->
        pagingData.map { remoteContent ->
            val type = object : TypeToken<T>() {}.type
            HttpManager.getGson().fromJson<T>(remoteContent.content, type)
        }
    }.cachedIn(viewModelScope)
}

@OptIn(ExperimentalPagingApi::class)
class RemoteRoomMediator<T>(
    private val remoteKey: String,
    private val initialKey: Int,
    private val database: RemoteCacheDatabase,
    private val requestAction: suspend (page: Int) -> List<T>,
    private val isSingle: Boolean = false
) : RemoteMediator<Int, RemoteContent>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RemoteContent>
    ): MediatorResult {
        return when (loadType) {
            // 首次访问 || PagingDataAdapter.refresh()
            LoadType.REFRESH -> {
                loadRefresh(loadType, state, isSingle)
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
        state: PagingState<Int, RemoteContent>,
        isSingle: Boolean
    ): MediatorResult {
        return loadDataTransaction(loadType, initialKey, isSingle)
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
            database.remoteKeyDao().getRemoteKey(remoteKey = remoteKey)
        }?.nextKey ?: run {
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        return loadDataTransaction(loadType, page)
    }

    private suspend fun loadDataTransaction(
        loadType: LoadType,
        page: Int,
        isSingle: Boolean = false,
    ): MediatorResult {
        try {
            val result = requestAction(page)
            val endOfPaginationReached = if (isSingle) true else result.isEmpty()

            val item = result.map {
                RemoteContent(
                    remoteKey = remoteKey,
                    content = HttpManager.getGson().toJson(it)
                )
            }

            // 插入数据库
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().clearRemoteKey(remoteKey)
                    database.remoteContentDao().clear(remoteKey)
                }
                val nextKey = if (endOfPaginationReached) null else page + 1
                val entity = RemoteKey(remoteKey = remoteKey, nextKey = nextKey)

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