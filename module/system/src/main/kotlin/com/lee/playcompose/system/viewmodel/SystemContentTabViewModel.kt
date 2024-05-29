package com.lee.playcompose.system.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * 体系内容子页面contentTab viewModel
 * @author jv.lee
 * @date 2022/5/18
 */
class SystemContentTabViewModel : ViewModel() {

    var viewStates by mutableStateOf(SystemContentTabViewState())
        private set

    fun dispatch(intent: SystemContentTabViewIntent) {
        when (intent) {
            is SystemContentTabViewIntent.SelectedTabIndex -> selectedTabIndex(intent.index)
        }
    }

    private fun selectedTabIndex(index: Int) {
        viewStates = viewStates.copy(selectedIndex = index)
    }
}

data class SystemContentTabViewState(
    val selectedIndex: Int = 0
)

sealed class SystemContentTabViewIntent {
    data class SelectedTabIndex(val index: Int) : SystemContentTabViewIntent()
}