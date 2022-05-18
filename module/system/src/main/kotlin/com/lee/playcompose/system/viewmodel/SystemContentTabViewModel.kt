package com.lee.playcompose.system.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * @author jv.lee
 * @date 2022/5/18
 * @description
 */
class SystemContentTabViewModel : ViewModel() {

    var viewStates by mutableStateOf(SystemContentTabViewState())
        private set

    fun dispatch(action: SystemContentTabViewAction) {
        when (action) {
            is SystemContentTabViewAction.SelectedTabIndex -> selectedTabIndex(action.index)
        }
    }

    private fun selectedTabIndex(index: Int) {
        viewStates = viewStates.copy(selectedIndex = index)
    }

}

data class SystemContentTabViewState(
    val selectedIndex: Int = 0,
)

sealed class SystemContentTabViewAction {
    data class SelectedTabIndex(val index: Int) : SystemContentTabViewAction()
}