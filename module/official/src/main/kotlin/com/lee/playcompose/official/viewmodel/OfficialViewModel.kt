package com.lee.playcompose.official.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.common.entity.Tab
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.ui.widget.UiStatus
import com.lee.playcompose.official.model.api.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/11
 * @description
 */
class OfficialViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(OfficialViewState())
        private set

    init {
        dispatch(OfficialViewAction.RequestTabData)
    }

    fun dispatch(action: OfficialViewAction) {
        when (action) {
            is OfficialViewAction.RequestTabData -> requestTabData()
        }
    }

    private fun requestTabData() {
        viewModelScope.launch {
            flow { emit(api.getOfficialTabsAsync()) }
                .onStart {
                    viewStates = viewStates.copy(pageStatus = UiStatus.Loading)
                }
                .catch {
                    viewStates = viewStates.copy(pageStatus = UiStatus.Failed)
                }
                .collect {
                    viewStates = viewStates.copy(tab = it.data)
                    viewStates = viewStates.copy(pageStatus = UiStatus.Complete)
                }
        }
    }

}

data class OfficialViewState(
    val tab: List<Tab> = emptyList(),
    val pageStatus: UiStatus = UiStatus.Loading
)

sealed class OfficialViewAction {
    object RequestTabData : OfficialViewAction()
}