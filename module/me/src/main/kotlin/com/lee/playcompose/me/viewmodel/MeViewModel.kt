package com.lee.playcompose.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService

/**
 * MePage ViewModel
 * @author jv.lee
 * @date 2022/5/5
 */
class MeViewModel : ViewModel() {
    val accountService: AccountService = ModuleService.find()
}