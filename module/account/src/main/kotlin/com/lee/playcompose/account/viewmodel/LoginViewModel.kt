package com.lee.playcompose.account.viewmodel

import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.constants.Constants
import com.lee.playcompose.account.model.api.ApiService
import com.lee.playcompose.base.tools.PreferencesTools
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description
 */
class LoginViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(LoginViewState())
        private set

    private val _viewEvents = Channel<LoginViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        setInputUsername()
    }

    fun dispatch(action: LoginViewAction) {
        when (action) {
            is LoginViewAction.ChangeUsername -> {
                changeUsername(action.username)
            }
            is LoginViewAction.ChangePassword -> {
                changePassword(action.password)
            }
            is LoginViewAction.RequestLogin -> {
                requestLogin()
            }
        }
    }

    private fun setInputUsername() {
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
            viewStates.copy(isLoginEnable = viewStates.username.isNotEmpty() && viewStates.password.isNotEmpty())
    }

    private fun requestLogin() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)

                // 校验输入格式
                if (TextUtils.isEmpty(viewStates.username) || TextUtils.isEmpty(viewStates.password)) {
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
            }.collect {
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
    val isLoginEnable: Boolean = false,
)

sealed class LoginViewEvent {
    data class LoginSuccess(val accountData: AccountData) : LoginViewEvent()
    data class LoginFailed(val message: String? = "") : LoginViewEvent()
}

sealed class LoginViewAction {
    data class ChangeUsername(val username: String) : LoginViewAction()
    data class ChangePassword(val password: String) : LoginViewAction()
    object RequestLogin : LoginViewAction()
}