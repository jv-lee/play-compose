package com.lee.playcompose.account.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.R
import com.lee.playcompose.account.constants.Constants
import com.lee.playcompose.account.model.api.ApiService
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.ktx.clearCache
import com.lee.playcompose.base.ktx.getCache
import com.lee.playcompose.base.ktx.putCache
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewIntent
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 账户操作ViewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class AccountViewModel : BaseMVIViewModel<AccountViewState, AccountViewEvent, AccountViewIntent>() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    override fun initViewState() = AccountViewState()

    override fun dispatch(intent: AccountViewIntent) {
        when (intent) {
            is AccountViewIntent.RequestAccountData -> {
                requestAccountData()
            }

            is AccountViewIntent.RequestLogout -> {
                requestLogout()
            }

            is AccountViewIntent.UpdateAccountStatus -> {
                updateAccountStatus(intent.accountData, intent.isLogin)
            }

            is AccountViewIntent.ClearLoginState -> {
                updateAccountStatus(null, false)
            }
        }
    }

    private fun requestAccountData() {
        viewModelScope.launch {
            flow {
                emit(api.getAccountInfoAsync().checkData())
            }.onStart {
                cacheManager.getCache<AccountData>(Constants.CACHE_KEY_ACCOUNT_DATA)?.let {
                    emit(it)
                }
            }.catch { error ->
                // 登陆token失效
                if (error.message == ApiConstants.REQUEST_TOKEN_ERROR_MESSAGE) {
                    updateAccountStatus(null, false)
                }
            }.collect {
                updateAccountStatus(it, true)
            }
        }
    }

    /**
     * 请求登出
     */
    private fun requestLogout() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)
                emit(api.getLogoutAsync().checkData())
            }.onStart {
                _viewStates = _viewStates.copy(isLoading = true)
            }.catch { error ->
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(AccountViewEvent.LogoutFailed(error.message))
            }.collect {
                _viewStates = _viewStates.copy(isLoading = false)
                updateAccountStatus(null, false)
                _viewEvents.send(
                    AccountViewEvent.LogoutSuccess(app.getString(R.string.account_logout_success))
                )
            }
        }
    }

    /**
     * @param accountData 账户数据
     * @param isLogin 登陆结果
     */
    private fun updateAccountStatus(accountData: AccountData?, isLogin: Boolean) {
        _viewStates = if (isLogin) {
            cacheManager.putCache(Constants.CACHE_KEY_ACCOUNT_DATA, accountData)
            PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, true)
            _viewStates.copy(accountData = accountData, isLogin = isLogin)
        } else {
            cacheManager.clearCache(Constants.CACHE_KEY_ACCOUNT_DATA)
            PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, false)
            _viewStates.copy(accountData = accountData, isLogin = isLogin)
        }
    }

}