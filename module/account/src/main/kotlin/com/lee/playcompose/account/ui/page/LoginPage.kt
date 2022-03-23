package com.lee.playcompose.account.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.account.R
import com.lee.playcompose.account.ui.composable.AccountSpacer
import com.lee.playcompose.account.ui.theme.ButtonLockColor
import com.lee.playcompose.account.ui.theme.ButtonTextColor
import com.lee.playcompose.account.viewmodel.LoginViewAction
import com.lee.playcompose.account.viewmodel.LoginViewEvent
import com.lee.playcompose.account.viewmodel.LoginViewModel
import com.lee.playcompose.account.viewmodel.LoginViewState
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.extensions.onTap
import com.lee.playcompose.common.entity.RegisterSuccessEvent
import com.lee.playcompose.common.extensions.hasBottomExpend
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.router.PageRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description 用户登陆页面
 */
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    val imeInsets = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.ime)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = viewModel.viewStates

    LaunchedEffect(Unit) {
        // 监听注册成功状态
        ChannelBus.bindChannel<RegisterSuccessEvent>(lifecycle)?.receiveAsFlow()?.collect {
            navController.popBackStack()
        }
        viewModel.viewEvents.collect { event ->
            when (event) {
                is LoginViewEvent.LoginSuccess -> {
                    // TODO AccountViewModel 更新登陆数据
                    keyboardController?.hide()
                    navController.popBackStack()
                }
                is LoginViewEvent.LoginFailed -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(imeInsets)
            .wrapContentSize(Alignment.Center)
            .background(AppTheme.colors.background)
            .onTap { keyboardController?.hide() }
    ) {
        Column {
            LoginTitle()
            LoginInputContent(viewState = viewState, usernameChange = {
                viewModel.dispatch(LoginViewAction.ChangeUsername(it))
            }, passwordChange = {
                viewModel.dispatch(LoginViewAction.ChangePassword(it))
            }, doneChange = {
                viewModel.dispatch(LoginViewAction.RequestLogin)
            })
            LoginFooter(viewState = viewState, gotoRegisterClick = {
                imeInsets.hasBottomExpend({ keyboardController?.hide() }, {
                    navController.navigate(PageRoute.Register.route)
                })
            }, loginClick = {
                viewModel.dispatch(LoginViewAction.RequestLogin)
            })
        }
    }
}

@Composable
private fun LoginTitle() {
    Text(
        text = stringResource(id = R.string.account_login_title),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = AppTheme.colors.accent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    )
}

@Composable
private fun LoginInputContent(
    viewState: LoginViewState,
    usernameChange: (String) -> Unit,
    passwordChange: (String) -> Unit,
    doneChange: KeyboardActionScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AppTextField(
                value = viewState.username,
                onValueChange = usernameChange,
                hintText = stringResource(id = R.string.account_username_text),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.vector_username),
                        contentDescription = null,
                        tint = ButtonLockColor
                    )
                }
            )
            AccountSpacer()
            AppTextField(
                value = viewState.password,
                onValueChange = passwordChange,
                hintText = stringResource(id = R.string.account_password_text),
                visualTransformation = PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onDone = doneChange),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.vector_password),
                        contentDescription = null,
                        tint = ButtonLockColor
                    )
                })
        }
    }
}

@Composable
private fun LoginFooter(
    viewState: LoginViewState,
    gotoRegisterClick: () -> Unit,
    loginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Text(
            text = stringResource(id = R.string.account_go_to_register_text),
            color = AppTheme.colors.focus,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable {
                    gotoRegisterClick()
                }
        )
        Button(
            onClick = loginClick,
            enabled = viewState.isLoginEnable,
            shape = RoundedCornerShape(OffsetRadiusMedium),
            modifier = Modifier.align(Alignment.CenterEnd),
            colors = ButtonDefaults.buttonColors(
                backgroundColor =
                if (viewState.isLoginEnable) AppTheme.colors.focus else ButtonLockColor
            ),
        ) {
            Text(
                text = stringResource(id = R.string.account_login_button),
                color = ButtonTextColor
            )
        }
    }
}