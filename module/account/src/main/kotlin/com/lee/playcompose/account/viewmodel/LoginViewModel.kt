package com.lee.playcompose.account.viewmodel

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.constants.Constants
import com.lee.playcompose.account.model.api.ApiService
import com.lee.playcompose.base.ktx.lowestTime
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 登陆功能viewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class LoginViewModel : BaseMVIViewModel<LoginViewState, LoginViewEvent, LoginViewIntent>() {

    private val api = createApi<ApiService>()

    init {
        restoreInputUsername()
    }

    override fun initViewState() = LoginViewState()

    override fun dispatch(intent: LoginViewIntent) {
        when (intent) {
            is LoginViewIntent.ChangeUsername -> {
                changeUsername(intent.username)
            }

            is LoginViewIntent.ChangePassword -> {
                changePassword(intent.password)
            }

            is LoginViewIntent.RequestLogin -> {
                requestLogin()
            }
        }
    }

    private fun restoreInputUsername() {
        viewModelScope.launch {
            flow {
                val username = PreferencesTools.get<String>(Constants.SP_KEY_SAVE_INPUT_USERNAME)
                emit(username)
            }.collect {
                _viewStates = _viewStates.copy(username = it)
                changeLoginEnable()
            }
        }
    }

    private fun changeUsername(username: String) {
        _viewStates = _viewStates.copy(username = username)
        changeLoginEnable()
    }

    private fun changePassword(password: String) {
        _viewStates = _viewStates.copy(password = password)
        changeLoginEnable()
    }

    private fun changeLoginEnable() {
        _viewStates =
            _viewStates.copy(
                isLoginEnable = _viewStates.username.isNotEmpty() && _viewStates.password.isNotEmpty()
            )
    }

    private fun requestLogin() {
        viewModelScope.launch {
            delay(300) // 延迟隐藏软键盘
            flow {
                // 校验输入格式
                if (TextUtils.isEmpty(_viewStates.username) ||
                    TextUtils.isEmpty(_viewStates.password)
                ) {
                    throw IllegalArgumentException("username || password is empty.")
                }

                api.postLoginAsync(_viewStates.username, _viewStates.password).checkData()
                val accountResponse = api.getAccountInfoAsync().checkData()
                emit(accountResponse)
            }.onStart {
                _viewStates = _viewStates.copy(isLoading = true)
            }.catch { error ->
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(LoginViewEvent.LoginFailed(error.message))
            }.lowestTime().collect {
                // 缓存用户明输入信息下次复用
                PreferencesTools.put(Constants.SP_KEY_SAVE_INPUT_USERNAME, _viewStates.username)
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(LoginViewEvent.LoginSuccess(it))
            }
        }
    }
}

data class LoginViewState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginEnable: Boolean = false
) : IViewState

sealed class LoginViewEvent : IViewEvent {
    data class LoginSuccess(val accountData: AccountData) : LoginViewEvent()
    data class LoginFailed(val message: String? = "") : LoginViewEvent()
}

sealed class LoginViewIntent : IViewIntent {
    data class ChangeUsername(val username: String) : LoginViewIntent()
    data class ChangePassword(val password: String) : LoginViewIntent()
    data object RequestLogin : LoginViewIntent()
}