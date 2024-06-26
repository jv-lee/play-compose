package com.lee.playcompose.me.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.utils.CacheUtil
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.me.R
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 设置页viewModel
 * @author jv.lee
 * @date 2022/4/2
 */
class SettingsViewModel : BaseMVIViewModel<SettingsViewEvent, SettingsViewIntent>() {

    var accountService = ModuleService.find<AccountService>()

    var viewStates by mutableStateOf(SettingsViewState())
        private set

    init {
        initCacheSize()
    }

    override fun dispatch(intent: SettingsViewIntent) {
        when (intent) {
            is SettingsViewIntent.VisibleCacheDialog -> {
                visibleCacheDialog(intent.visibility)
            }

            is SettingsViewIntent.VisibleLogoutDialog -> {
                visibleLogoutDialog(intent.visibility)
            }

            is SettingsViewIntent.RequestClearCache -> {
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
                    app.getString(
                        if (it) R.string.settings_clear_success else R.string.settings_clear_failed
                    )
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
    val totalCacheSize: String = ""
) : IViewState

sealed class SettingsViewEvent : IViewEvent {
    data class ClearCacheResult(val message: String) : SettingsViewEvent()
}

sealed class SettingsViewIntent : IViewIntent {
    data class VisibleCacheDialog(val visibility: Boolean) : SettingsViewIntent()
    data class VisibleLogoutDialog(val visibility: Boolean) : SettingsViewIntent()
    object RequestClearCache : SettingsViewIntent()
}