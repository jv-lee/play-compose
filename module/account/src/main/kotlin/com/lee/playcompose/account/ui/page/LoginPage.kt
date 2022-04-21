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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.account.R
import com.lee.playcompose.account.constants.Constants.REQUEST_KEY_LOGIN
import com.lee.playcompose.account.ui.composable.AccountSpacer
import com.lee.playcompose.account.ui.theme.ButtonLockColor
import com.lee.playcompose.account.ui.theme.ButtonTextColor
import com.lee.playcompose.account.viewmodel.*
import com.lee.playcompose.base.extensions.*
import com.lee.playcompose.common.entity.AccountViewAction
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.router.RoutePage
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description 用户登陆页面
 */
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(),
    accountViewModel: AccountViewModel = activityViewModel()
) {
    val imeInsets = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.ime)
    val imePadding = rememberImePaddingValue(imeInsets = imeInsets)
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = viewModel.viewStates

    // 监听注册成功状态
    navController.forResult<Int>(key = REQUEST_KEY_LOGIN) {
        navController.popBackStack()
    }

    // 页面单向事件监听
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is LoginViewEvent.LoginSuccess -> {
                    accountViewModel.dispatch(
                        AccountViewAction.UpdateAccountStatus(event.accountData, true)
                    )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = imePadding.dp)
            .wrapContentSize(Alignment.Center)
            .background(AppTheme.colors.background)
            .onTap { keyboardController?.hide() })
    {
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
                navController.navigate(RoutePage.Account.Register.route)
            })
        }, loginClick = {
            viewModel.dispatch(LoginViewAction.RequestLogin)
        })
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
        backgroundColor = AppTheme.colors.item,
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Column {
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
                    keyboardType = KeyboardType.Text,
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