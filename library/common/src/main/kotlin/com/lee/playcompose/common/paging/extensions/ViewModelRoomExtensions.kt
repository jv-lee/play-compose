package com.lee.playcompose.common.paging.extensions

import android.util.Log
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
 * @description
 */
@OptIn(ExperimentalPagingApi::class)
inline fun <reified T : Any> ViewModel.localPaging(
    config: PagingConfig = PagingConfig(20),
    initialKey: Int = 0,
    remoteKey: String,
    crossinline requestAction: suspend (page: Int) -> List<T>
): Flow<PagingData<T>> {
    val database = RemoteCacheDatabase.get()
    return Pager(
        config = config,
        initialKey = initialKey,
        remoteMediator = createRemoteMediator(database, initialKey, remoteKey, requestAction)
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
inline fun <reified T : Any> createRemoteMediator(
    database: RemoteCacheDatabase,
    initialKey: Int,
    remoteKey: String,
    crossinline requestAction: suspend (page: Int) -> List<T>
): RemoteMediator<Int, RemoteContent> {
    return object : RemoteMediator<Int, RemoteContent>() {
        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, RemoteContent>
        ): MediatorResult {
            try {
                // 第一步： 判断 LoadType
                val pageKey = when (loadType) {
                    // 首次访问 或者调用 PagingDataAdapter.refresh()
                    LoadType.REFRESH -> {
                        Log.i("jv.lee", "REFRESH")
                        null
                    }
                    // 在当前加载的数据集的开头加载数据时
                    LoadType.PREPEND -> {
                        Log.i("jv.lee", "PREPEND")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    LoadType.APPEND -> { // 下来加载更多时触发
                        Log.i("jv.lee", "APPEND")
                        database.withTransaction {
                            database.remoteKeyDao().getRemoteKey(remoteKey = remoteKey)
                        }?.nextKey ?: kotlin.run {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                    }
                }

                // 第二步： 请问网络分页数据
                val page = pageKey ?: 0
                val result = requestAction(page)

                val endOfPaginationReached = result.isEmpty()

                val item = result.map {
                    RemoteContent(
                        remoteKey = remoteKey,
                        content = HttpManager.getGson().toJson(it)
                    )
                }

                // 第三步： 插入数据库
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteContentDao().clear(remoteKey)
                    }
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val entity = RemoteKey(remoteKey = remoteKey, nextKey = nextKey)

                    database.remoteKeyDao().clearRemoteKey(remoteKey)
                    database.remoteKeyDao().insert(entity)
                    database.remoteContentDao().insertList(item)
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } catch (e: Exception) {
                return database.withTransaction {
                    val count = database.remoteContentDao().getCount()
                    val remoteKey = database.remoteKeyDao().getRemoteKey(remoteKey = remoteKey)
                    Log.i("jv.lee", "count:$count,remoteKey:$remoteKey")
                    return@withTransaction if (count > 0) {
                        MediatorResult.Success(endOfPaginationReached = false)
                    } else {
                        MediatorResult.Error(e)
                    }
                }
            }
        }

    }
}