package com.lee.playcompose.me.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.utils.CacheUtil
import com.lee.playcompose.me.R
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/4/2
 * @description
 */
class SettingsViewModel : ViewModel() {

    var accountService = ModuleService.find<AccountService>()

    var viewStates by mutableStateOf(SettingsViewState())
        private set

    private val _viewEvents = Channel<SettingsViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(SettingsViewAction.InitCacheSize)
    }

    fun dispatch(action: SettingsViewAction) {
        when (action) {
            is SettingsViewAction.InitCacheSize -> {
                initCacheSize()
            }
            is SettingsViewAction.RequestClearCache -> {
                requestClearCache()
            }
        }
    }

    private fun initCacheSize() {
        val totalCacheSize = CacheUtil.getTotalCacheSize(app)
        viewStates = viewStates.copy(totalCacheSize = totalCacheSize)
    }

    private fun requestClearCache() {
        viewModelScope.launch {
            flow {
                emit(CacheUtil.clearAllCache(app))
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch {
                viewStates = viewStates.copy(isLoading = false)
            }.collect {
                val message =
                    app.getString(if (it) R.string.settings_clear_success else R.string.settings_clear_failed)
                _viewEvents.send(SettingsViewEvent.ClearCacheResult(message = message))
                viewStates = viewStates.copy(isLoading = false)
                dispatch(SettingsViewAction.InitCacheSize)
            }
        }
    }

}

data class SettingsViewState(
    val isLoading: Boolean = false,
    val totalCacheSize: String = ""
)

sealed class SettingsViewEvent {
    data class ClearCacheResult(val message: String) : SettingsViewEvent()
}

sealed class SettingsViewAction {
    object RequestClearCache : SettingsViewAction()
    object InitCacheSize : SettingsViewAction()
}