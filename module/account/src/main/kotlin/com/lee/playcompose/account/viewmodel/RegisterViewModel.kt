package com.lee.playcompose.account.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.model.api.ApiService
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
class RegisterViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(RegisterViewState())
        private set

    private val _viewEvents = Channel<RegisterViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: RegisterViewAction) {
        when (action) {
            is RegisterViewAction.ChangeUsername -> {
                changeUsername(action.username)
            }
            is RegisterViewAction.ChangePassword -> {
                changePassword(action.password)
            }
            is RegisterViewAction.ChangeRePassword -> {
                changeRePassword(action.rePassword)
            }
            is RegisterViewAction.RequestRegister -> {
                requestRegister()
            }
        }
    }

    private fun changeUsername(username: String) {
        viewStates = viewStates.copy(username = username)
        changeRegisterEnable()
    }

    private fun changePassword(password: String) {
        viewStates = viewStates.copy(password = password)
        changeRegisterEnable()
    }

    private fun changeRePassword(rePassword: String) {
        viewStates = viewStates.copy(rePassword = rePassword)
        changeRegisterEnable()
    }

    private fun changeRegisterEnable() {
        viewStates = viewStates.copy(
            isRegisterEnable = viewStates.username.isNotEmpty() &&
                    viewStates.password.isNotEmpty() &&
                    viewStates.rePassword.isNotEmpty()
        )
    }

    private fun requestRegister() {
        viewModelScope.launch {
            flow {
                kotlinx.coroutines.delay(500)

                // 校验输入格式
                if (viewStates.username.isEmpty() ||
                    viewStates.password.isEmpty() ||
                    viewStates.rePassword.isEmpty()
                ) {
                    throw IllegalArgumentException("username || password || repassword is empty.")
                }

                api.postRegisterAsync(
                    viewStates.username,
                    viewStates.password,
                    viewStates.rePassword
                ).checkData()

                val accountResponse = api.getAccountInfoAsync().checkData()
                emit(accountResponse)
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(RegisterViewEvent.RegisterFailed(error.message))
            }.collect {
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(RegisterViewEvent.RegisterSuccess(it))
            }
        }
    }
}

data class RegisterViewState(
    val username: String = "",
    val password: String = "",
    val rePassword: String = "",
    val isLoading: Boolean = false,
    val isRegisterEnable: Boolean = false,
)

sealed class RegisterViewEvent {
    data class RegisterSuccess(val accountData: AccountData) : RegisterViewEvent()
    data class RegisterFailed(val message: String? = "") : RegisterViewEvent()
}

sealed class RegisterViewAction {
    data class ChangeUsername(val username: String) : RegisterViewAction()
    data class ChangePassword(val password: String) : RegisterViewAction()
    data class ChangeRePassword(val rePassword: String) : RegisterViewAction()
    object RequestRegister : RegisterViewAction()
}