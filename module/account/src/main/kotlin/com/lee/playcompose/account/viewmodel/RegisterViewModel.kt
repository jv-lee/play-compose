package com.lee.playcompose.account.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.account.model.api.ApiService
import com.lee.playcompose.base.extensions.lowestTime
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.entity.AccountData
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 注册功能viewModel
 * @author jv.lee
 * @date 2022/3/23
 */
class RegisterViewModel : BaseMVIViewModel<RegisterViewEvent, RegisterViewIntent>() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(RegisterViewState())
        private set

    override fun dispatch(intent: RegisterViewIntent) {
        when (intent) {
            is RegisterViewIntent.ChangeUsername -> {
                changeUsername(intent.username)
            }

            is RegisterViewIntent.ChangePassword -> {
                changePassword(intent.password)
            }

            is RegisterViewIntent.ChangeRePassword -> {
                changeRePassword(intent.rePassword)
            }

            is RegisterViewIntent.RequestRegister -> {
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
            delay(300) // 延迟隐藏软键盘
            flow {
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
            }.lowestTime().collect {
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
    val isRegisterEnable: Boolean = false
) : IViewState

sealed class RegisterViewEvent : IViewEvent {
    data class RegisterSuccess(val accountData: AccountData) : RegisterViewEvent()
    data class RegisterFailed(val message: String?) : RegisterViewEvent()
}

sealed class RegisterViewIntent : IViewIntent {
    data class ChangeUsername(val username: String) : RegisterViewIntent()
    data class ChangePassword(val password: String) : RegisterViewIntent()
    data class ChangeRePassword(val rePassword: String) : RegisterViewIntent()
    object RequestRegister : RegisterViewIntent()
}