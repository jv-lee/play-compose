package com.lee.playcompose.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.extensions.cacheFlow
import com.lee.playcompose.common.entity.Tab
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.ui.widget.UiStatus
import com.lee.playcompose.project.constants.Constants.CACHE_KEY_PROJECT_TAB
import com.lee.playcompose.project.model.api.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2022/3/11
 */
class ProjectViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    private val cacheManager = CacheManager.getDefault()

    var viewStates by mutableStateOf(ProjectViewState())
        private set

    init {
        dispatch(ProjectViewAction.RequestTabData)
    }

    fun dispatch(action: ProjectViewAction) {
        when (action) {
            is ProjectViewAction.RequestTabData -> requestTabData()
            is ProjectViewAction.SelectedTabIndex -> selectedTabIndex(action.index)
        }
    }

    private fun requestTabData() {
        viewModelScope.launch {
            cacheManager.cacheFlow(CACHE_KEY_PROJECT_TAB) {
                api.getProjectTabsAsync()
            }.onStart {
                viewStates = viewStates.copy(uiStatus = UiStatus.Loading)
            }.catch {
                viewStates = viewStates.copy(uiStatus = UiStatus.Failed)
            }.collect {
                viewStates = viewStates.copy(tab = it.data)
                viewStates = viewStates.copy(uiStatus = UiStatus.Complete)
            }
        }
    }

    private fun selectedTabIndex(index: Int) {
        viewStates = viewStates.copy(selectedIndex = index)
    }

}

data class ProjectViewState(
    val selectedIndex: Int = 0,
    val tab: List<Tab> = emptyList(),
    val uiStatus: UiStatus = UiStatus.Loading
)

sealed class ProjectViewAction {
    object RequestTabData : ProjectViewAction()
    data class SelectedTabIndex(val index: Int) : ProjectViewAction()
}