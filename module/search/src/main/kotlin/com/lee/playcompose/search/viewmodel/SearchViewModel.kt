package com.lee.playcompose.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.cacheFlow
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.SearchHistory
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.search.constants.Constants
import com.lee.playcompose.search.model.api.ApiService
import com.lee.playcompose.search.model.db.SearchDatabase
import com.lee.playcompose.search.model.entity.SearchHotUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 搜索页面viewModel
 * @author jv.lee
 * @date 2022/3/18
 */
class SearchViewModel : BaseMVIViewModel<SearchViewState, SearchViewEvent, SearchViewIntent>() {

    private val api = createApi<ApiService>()
    private val searchHistoryDao = SearchDatabase.get().searchHistoryDao()
    private val cacheManager = CacheManager.getDefault()

    init {
        requestSearchHotData()
        requestSearchHistoryData()
    }

    override fun initViewState() = SearchViewState()

    override fun dispatch(intent: SearchViewIntent) {
        when (intent) {
            is SearchViewIntent.ChangeSearchKey -> {
                changeSearchKey(intent.key)
            }
            is SearchViewIntent.NavigationSearchKey -> {
                navigationSearchKey(intent.key)
            }
            is SearchViewIntent.DeleteSearchHistory -> {
                deleteSearchHistory(intent.key)
            }
            is SearchViewIntent.ClearSearchHistory -> {
                clearSearchHistory()
            }
        }
    }

    /**
     * 获取搜索热门标签
     */
    private fun requestSearchHotData() {
        viewModelScope.launch {
            cacheManager.cacheFlow(Constants.CACHE_KEY_SEARCH_HOT) {
                api.getSearchHotAsync().checkData()
            }.map { list ->
                list.map { SearchHotUI(it.name) }
            }.catch { error ->
                _viewEvents.send(SearchViewEvent.FailedEvent(error = error))
            }.collect {
                _viewStates = _viewStates.copy(searchHot = it)
            }
        }
    }

    /**
     * 获取搜索历史数据
     */
    private fun requestSearchHistoryData() {
        viewModelScope.launch {
            flow {
                emit(searchHistoryDao.querySearchHistory())
            }.collect {
                _viewStates = _viewStates.copy(searchHistory = it)
            }
        }
    }

    /**
     * 搜索文本监听处理
     */
    private fun changeSearchKey(key: String) {
        _viewStates = _viewStates.copy(searchKey = key)
    }

    /**
     * 导航到搜索结果页
     * @param key 搜索key
     */
    private fun navigationSearchKey(key: String) {
        if (key.isEmpty()) return

        viewModelScope.launch {
            _viewEvents.send(SearchViewEvent.NavigationSearch(key))
            addSearchHistory(key)
        }
    }

    /**
     * 添加搜索记录
     * @param key 被搜索的key
     */
    private fun addSearchHistory(key: String, isDelay: Long = 500) {
        viewModelScope.launch {
            flow {
                delay(isDelay)
                emit(searchHistoryDao.insert(SearchHistory(key = key)))
            }.collect {
                requestSearchHistoryData()
            }
        }
    }

    /**
     * 删除搜索记录
     * @param key 被搜索的key
     */
    private fun deleteSearchHistory(key: String) {
        viewModelScope.launch {
            flow {
                emit(searchHistoryDao.delete(SearchHistory(key = key)))
            }.collect {
                requestSearchHistoryData()
            }
        }
    }

    /**
     * 清空所有搜索记录
     */
    private fun clearSearchHistory() {
        viewModelScope.launch {
            flow {
                emit(searchHistoryDao.clearSearchHistory())
            }.collect {
                requestSearchHistoryData()
            }
        }
    }
}

data class SearchViewState(
    val searchKey: String = "",
    val searchHot: List<SearchHotUI> = emptyList(),
    val searchHistory: List<SearchHistory> = emptyList()
) : IViewState

sealed class SearchViewEvent : IViewEvent {
    data class NavigationSearch(val key: String) : SearchViewEvent()
    data class FailedEvent(val error: Throwable) : SearchViewEvent()
}

sealed class SearchViewIntent : IViewIntent {
    data class NavigationSearchKey(val key: String) : SearchViewIntent()
    data class DeleteSearchHistory(val key: String) : SearchViewIntent()
    data object ClearSearchHistory : SearchViewIntent()
    data class ChangeSearchKey(val key: String) : SearchViewIntent()
}