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
 *
 * @author jv.lee
 * @date 2022/4/2
 */
class SettingsViewModel : ViewModel() {

    var accountService = ModuleService.find<AccountService>()

    var viewStates by mutableStateOf(SettingsViewState())
        private set

    private val _viewEvents = Channel<SettingsViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        initCacheSize()
    }

    fun dispatch(action: SettingsViewAction) {
        when (action) {
            is SettingsViewAction.VisibleCacheDialog -> {
                visibleCacheDialog(action.visibility)
            }
            is SettingsViewAction.VisibleLogoutDialog -> {
                visibleLogoutDialog(action.visibility)
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

    private fun visibleCacheDialog(visibility: Boolean) {
        viewModelScope.launch {
            viewStates = viewStates.copy(isCacheConfirm = visibility)
        }
    }

    private fun visibleLogoutDialog(visibility: Boolean) {
        viewModelScope.launch {
            viewStates = viewStates.copy(isLogoutConfirm = visibility)
        }
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
                viewStates = viewStates.copy(isLoading = false, isCacheConfirm = false)
                initCacheSize()
            }
        }
    }

}

data class SettingsViewState(
    val isLoading: Boolean = false,
    val isCacheConfirm: Boolean = false,
    val isLogoutConfirm: Boolean = false,
    val totalCacheSize: String = "",
)

sealed class SettingsViewEvent {
    data class ClearCacheResult(val message: String) : SettingsViewEvent()
}

sealed class SettingsViewAction {
    data class VisibleCacheDialog(val visibility: Boolean) : SettingsViewAction()
    data class VisibleLogoutDialog(val visibility: Boolean) : SettingsViewAction()
    object RequestClearCache : SettingsViewAction()
}