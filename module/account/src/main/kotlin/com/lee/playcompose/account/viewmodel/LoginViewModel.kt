package com.lee.playcompose.account.viewmodel

import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.constants.Constants
import com.lee.playcompose.account.model.api.ApiService
import com.lee.playcompose.base.extensions.lowestTime
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 登陆功能viewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class LoginViewModel : BaseMVIViewModel<LoginViewEvent, LoginViewIntent>() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(LoginViewState())
        private set

    init {
        restoreInputUsername()
    }

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
                viewStates = viewStates.copy(username = it)
                changeLoginEnable()
            }
        }
    }

    private fun changeUsername(username: String) {
        viewStates = viewStates.copy(username = username)
        changeLoginEnable()
    }

    private fun changePassword(password: String) {
        viewStates = viewStates.copy(password = password)
        changeLoginEnable()
    }

    private fun changeLoginEnable() {
        viewStates =
            viewStates.copy(
                isLoginEnable = viewStates.username.isNotEmpty() && viewStates.password.isNotEmpty()
            )
    }

    private fun requestLogin() {
        viewModelScope.launch {
            delay(300) // 延迟隐藏软键盘
            flow {
                // 校验输入格式
                if (TextUtils.isEmpty(viewStates.username) ||
                    TextUtils.isEmpty(viewStates.password)
                ) {
                    throw IllegalArgumentException("username || password is empty.")
                }

                api.postLoginAsync(viewStates.username, viewStates.password).checkData()
                val accountResponse = api.getAccountInfoAsync().checkData()
                emit(accountResponse)
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(LoginViewEvent.LoginFailed(error.message))
            }.lowestTime().collect {
                // 缓存用户明输入信息下次复用
                PreferencesTools.put(Constants.SP_KEY_SAVE_INPUT_USERNAME, viewStates.username)
                viewStates = viewStates.copy(isLoading = false)
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
    object RequestLogin : LoginViewIntent()
}