package com.lee.playcompose.account

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import com.lee.playcompose.account.constants.Constants
import com.lee.playcompose.account.viewmodel.AccountViewModel
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.ktx.getCache
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.entity.AccountViewIntent
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.service.AccountService
import kotlinx.coroutines.flow.Flow

/**
 * 账户对外提供功能实现接口
 * @author jv.lee
 * @date 2022/3/23
 */
@AutoService(AccountService::class)
class AccountServiceImpl : AccountService {

    override fun getAccountViewStates(activity: FragmentActivity): AccountViewState {
        return activity.viewModels<AccountViewModel>().value.viewStates()
    }

    override fun getAccountViewEvents(activity: FragmentActivity): Flow<AccountViewEvent> {
        return activity.viewModels<AccountViewModel>().value.viewEvents
    }

    override fun requestAccountInfo(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewIntent.RequestAccountData)
    }

    override fun requestLogout(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewIntent.RequestLogout)
    }

    override fun clearLoginState(activity: FragmentActivity) {
        activity.viewModels<AccountViewModel>().value.dispatch(AccountViewIntent.ClearLoginState)
    }

    override fun getAccountInfo(): AccountData? {
        return CacheManager.getDefault().getCache(Constants.CACHE_KEY_ACCOUNT_DATA)
    }

    override fun getUserId(): Long {
        return getAccountInfo()?.run { userInfo.id } ?: kotlin.run { 0 }
    }

    override fun isLogin(): Boolean {
        return PreferencesTools.get(Constants.SP_KEY_IS_LOGIN)
    }
}