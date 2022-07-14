package com.lee.playcompose.me.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.me.model.entity.MeItem
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService

/**
 * MePage ViewModel
 * @author jv.lee
 * @date 2022/5/5
 */
class MeViewModel : ViewModel() {

    var viewStates by mutableStateOf(MeViewState())
        private set

    val accountService: AccountService = ModuleService.find()
}

data class MeViewState(val meItems: List<MeItem> = MeItem.getMeItems())