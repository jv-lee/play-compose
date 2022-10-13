package com.lee.playcompose.search.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.cacheFlow
import com.lee.playcompose.common.entity.SearchHistory
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.search.constants.Constants
import com.lee.playcompose.search.model.api.ApiService
import com.lee.playcompose.search.model.db.SearchDatabase
import com.lee.playcompose.search.model.entity.SearchHotUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 搜索页面viewModel
 * @author jv.lee
 * @date 2022/3/18
 */
class SearchViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val searchHistoryDao = SearchDatabase.get().searchHistoryDao()
    private val cacheManager = CacheManager.getDefault()

    var viewStates by mutableStateOf(SearchViewState())
        private set

    private val _viewEvents = Channel<SearchViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        requestSearchHotData()
        requestSearchHistoryData()
    }

    fun dispatch(action: SearchViewAction) {
        when (action) {
            is SearchViewAction.ChangeSearchKey -> {
                changeSearchKey(action.key)
            }
            is SearchViewAction.NavigationSearchKey -> {
                navigationSearchKey(action.key)
            }
            is SearchViewAction.DeleteSearchHistory -> {
                deleteSearchHistory(action.key)
            }
            is SearchViewAction.ClearSearchHistory -> {
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
                viewStates = viewStates.copy(searchHot = it)
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
                viewStates = viewStates.copy(searchHistory = it)
            }
        }
    }

    /**
     * 搜索文本监听处理
     */
    private fun changeSearchKey(key: String) {
        viewStates = viewStates.copy(searchKey = key)
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
)

sealed class SearchViewEvent {
    data class NavigationSearch(val key: String) : SearchViewEvent()
    data class FailedEvent(val error: Throwable) : SearchViewEvent()
}

sealed class SearchViewAction {
    data class NavigationSearchKey(val key: String) : SearchViewAction()
    data class DeleteSearchHistory(val key: String) : SearchViewAction()
    object ClearSearchHistory : SearchViewAction()
    data class ChangeSearchKey(val key: String) : SearchViewAction()
}