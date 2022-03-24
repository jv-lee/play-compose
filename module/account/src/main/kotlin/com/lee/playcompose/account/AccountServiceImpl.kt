package com.lee.playcompose.account

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import com.lee.playcompose.account.viewmodel.AccountViewModel
import com.lee.playcompose.common.entity.AccountViewAction
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.service.AccountService
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description
 */
@AutoService(AccountService::class)
class AccountServiceImpl : AccountService {
    override fun getAccountViewStates(activity: FragmentActivity): AccountViewState {
        return activity.viewModels<AccountViewModel>().value.viewStates
    }

    override fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent> {
        return activity.viewModels<AccountViewModel>().value.viewEvents
    }

    override suspend fun requestAccountInfo(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewAction.RequestAccountData)
    }

    override suspend fun requestLogout(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewAction.RequestLogout)
    }
}