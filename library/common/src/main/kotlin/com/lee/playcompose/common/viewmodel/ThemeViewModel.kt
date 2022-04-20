package com.lee.playcompose.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.base.tools.DarkModeTools

/**
 * @author jv.lee
 * @date 2022/4/20
 * @description app主题控制viewModel
 */
class ThemeViewModel : ViewModel() {

    var viewStates by mutableStateOf(ThemeViewState())
        private set

    init {
        initDarkTheme()
    }

    fun dispatch(action: ThemeViewAction) {
        when (action) {
            is ThemeViewAction.UpdateDarkAction -> {
                updateDark(action.enable)
            }
            is ThemeViewAction.UpdateSystemAction -> {
                updateSystem(action.enable)
            }
        }
    }

    private fun updateDark(enable: Boolean) {
        DarkModeTools.get().updateNightTheme(enable = enable)
        initDarkTheme()
    }

    private fun updateSystem(enable: Boolean) {
        DarkModeTools.get().updateSystemTheme(enable = enable)
        initDarkTheme()
    }

    private fun initDarkTheme() {
        viewStates = viewStates.copy(
            isDark = DarkModeTools.get().isDarkTheme(),
            isSystem = DarkModeTools.get().isSystemTheme()
        )
    }

}

data class ThemeViewState(val isDark: Boolean = false, val isSystem: Boolean = false)

sealed class ThemeViewAction {
    data class UpdateDarkAction(val enable: Boolean) : ThemeViewAction()
    data class UpdateSystemAction(val enable: Boolean) : ThemeViewAction()
}