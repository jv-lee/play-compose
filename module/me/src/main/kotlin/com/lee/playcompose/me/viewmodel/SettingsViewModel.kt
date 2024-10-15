package com.lee.playcompose.me.viewmodel

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
class SettingsViewModel :
    BaseMVIViewModel<SettingsViewState, SettingsViewEvent, SettingsViewIntent>() {

    var accountService = ModuleService.find<AccountService>()

    init {
        initCacheSize()
    }

    override fun initViewState() = SettingsViewState()

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
        _viewStates = _viewStates.copy(totalCacheSize = totalCacheSize)
    }

    private fun visibleCacheDialog(visibility: Boolean) {
        viewModelScope.launch {
            _viewStates = _viewStates.copy(isCacheConfirm = visibility)
        }
    }

    private fun visibleLogoutDialog(visibility: Boolean) {
        viewModelScope.launch {
            _viewStates = _viewStates.copy(isLogoutConfirm = visibility)
        }
    }

    private fun requestClearCache() {
        viewModelScope.launch {
            flow {
                emit(CacheUtil.clearAllCache(app))
            }.onStart {
                _viewStates = _viewStates.copy(isLoading = true)
            }.catch {
                _viewStates = _viewStates.copy(isLoading = false)
            }.collect {
                val message =
                    app.getString(
                        if (it) R.string.settings_clear_success else R.string.settings_clear_failed
                    )
                _viewEvents.send(SettingsViewEvent.ClearCacheResult(message = message))
                _viewStates = _viewStates.copy(isLoading = false, isCacheConfirm = false)
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
    data object RequestClearCache : SettingsViewIntent()
}