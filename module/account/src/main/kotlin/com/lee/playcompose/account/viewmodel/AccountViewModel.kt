package com.lee.playcompose.account.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.R
import com.lee.playcompose.account.constants.Constants
import com.lee.playcompose.account.model.api.ApiService
import com.lee.playcompose.base.cache.CacheManager
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.clearCache
import com.lee.playcompose.base.extensions.getCache
import com.lee.playcompose.base.extensions.putCache
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.entity.AccountViewAction
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 账户操作ViewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class AccountViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    var viewStates by mutableStateOf(AccountViewState())
        private set

    private val _viewEvents = Channel<AccountViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    suspend fun dispatch(action: AccountViewAction) {
        when (action) {
            is AccountViewAction.RequestAccountData -> {
                requestAccountData()
            }
            is AccountViewAction.RequestLogout -> {
                requestLogout()
            }
            is AccountViewAction.UpdateAccountStatus -> {
                updateAccountStatus(action.accountData, action.isLogin)
            }
            is AccountViewAction.ClearLoginState -> {
                updateAccountStatus(null, false)
            }
        }
    }

    private suspend fun requestAccountData() {
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

    /**
     * 请求登出
     */
    private fun requestLogout() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)
                emit(api.getLogoutAsync().checkData())
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(AccountViewEvent.LogoutFailed(error.message))
            }.collect {
                viewStates = viewStates.copy(isLoading = false)
                updateAccountStatus(null, false)
                _viewEvents.send(AccountViewEvent.LogoutSuccess(app.getString(R.string.account_logout_success)))
            }
        }
    }

    /**
     * @param accountData 账户数据
     * @param isLogin 登陆结果
     */
    private fun updateAccountStatus(accountData: AccountData?, isLogin: Boolean) {
        viewStates = if (isLogin) {
            cacheManager.putCache(Constants.CACHE_KEY_ACCOUNT_DATA, accountData)
            PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, true)
            viewStates.copy(accountData = accountData, isLogin = isLogin)
        } else {
            cacheManager.clearCache(Constants.CACHE_KEY_ACCOUNT_DATA)
            PreferencesTools.put(Constants.SP_KEY_IS_LOGIN, false)
            viewStates.copy(accountData = accountData, isLogin = isLogin)
        }
    }

}