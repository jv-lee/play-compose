package com.lee.playcompose.me.viewmodel

import androidx.lifecycle.ViewModel
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService

/**
 * @author jv.lee
 * @date 2022/5/5
 * @description
 */
class MeViewModel : ViewModel() {
    val accountService: AccountService = ModuleService.find()
}