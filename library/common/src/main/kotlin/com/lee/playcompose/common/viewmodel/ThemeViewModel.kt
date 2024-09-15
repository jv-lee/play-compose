package com.lee.playcompose.common.viewmodel

import android.annotation.SuppressLint
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.playcompose.base.tools.DarkModeTools

/**
 * app主题控制viewModel
 * @author jv.lee
 * @date 2022/4/20
 */
@SuppressLint("StaticFieldLeak")
class ThemeViewModel(private val context: Context) : ViewModel(), ComponentCallbacks {

    private val darkModeTools: DarkModeTools = DarkModeTools.get()

    var viewStates by mutableStateOf(ThemeViewState())
        private set

    init {
        context.registerComponentCallbacks(this)
        initDarkTheme()
        changeFontScale(context.resources.configuration.fontScale)
    }

    override fun onCleared() {
        context.unregisterComponentCallbacks(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        changeFontScale(newConfig.fontScale)
    }

    override fun onLowMemory() {}

    fun dispatch(intent: ThemeViewIntent) {
        when (intent) {
            is ThemeViewIntent.UpdateDarkTheme -> {
                onUpdateDarkTheme(intent.enable)
            }

            is ThemeViewIntent.UpdateSystemTheme -> {
                onUpdateSystemTheme(intent.enable)
            }

            is ThemeViewIntent.ResetThemeStatus -> {
                initDarkTheme()
            }
        }
    }

    private fun onUpdateDarkTheme(enable: Boolean) {
        darkModeTools.updateDarkTheme(enable = enable)
        initDarkTheme()
    }

    private fun onUpdateSystemTheme(enable: Boolean) {
        darkModeTools.updateSystemTheme(enable = enable)
        initDarkTheme()
    }

    private fun initDarkTheme() {
        val isDark = darkModeTools.isDarkTheme()
        val isSystem = darkModeTools.isSystemTheme()
        viewStates = viewStates.copy(
            isDark = isDark,
            isSystem = isSystem,
            statusBarDarkContentEnabled = !isDark
        )
    }

    /**
     * 获取当前字体缩放值更新
     *
     * @param fontScale 缩放指数
     */
    private fun changeFontScale(fontScale: Float) {
        if (viewStates.fontScale != fontScale) {
            viewStates = viewStates.copy(fontScale = fontScale)
        }
    }

    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ThemeViewModel(context) as T
        }
    }
}

data class ThemeViewState(
    val fontScale: Float = 1.0f,
    val isDark: Boolean = false,
    val isSystem: Boolean = false,
    val statusBarDarkContentEnabled: Boolean = false
)

sealed class ThemeViewIntent {
    data class UpdateDarkTheme(val enable: Boolean) : ThemeViewIntent()
    data class UpdateSystemTheme(val enable: Boolean) : ThemeViewIntent()
    object ResetThemeStatus : ThemeViewIntent()
}