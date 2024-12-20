package com.lee.playcompose.account.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.account.R
import com.lee.playcompose.account.ui.composable.AccountSpacer
import com.lee.playcompose.account.ui.theme.ButtonLockColor
import com.lee.playcompose.account.ui.theme.ButtonTextColor
import com.lee.playcompose.account.viewmodel.*
import com.lee.playcompose.base.ktx.*
import com.lee.playcompose.common.entity.AccountViewIntent
import com.lee.playcompose.common.ktx.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs

/** 注册界面注册成功后回调登陆页面回传key（通知登陆页面已注册成功） */
const val REQUEST_KEY_LOGIN = "requestKey:login"

/**
 * 用户登陆页面
 * @author jv.lee
 * @date 2022/3/23
 */
@Composable
fun LoginPage(
    navController: NavController = LocalNavController.current,
    viewModel: LoginViewModel = viewModel(),
    accountViewModel: AccountViewModel = activityViewModel()
) {
    val imeInsets = WindowInsets.ime.asPaddingValues()
    val focusManager = LocalFocusManager.current
    val viewState = viewModel.viewStates()

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
                        AccountViewIntent.UpdateAccountStatus(event.accountData, true)
                    )
                    focusManager.clearFocus()
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
            .background(ColorsTheme.colors.background)
            .onTap { focusManager.clearFocus() }
            .fillMaxSize()
            .imePadding()
            .wrapContentSize(Alignment.Center)
    ) {
        LoginTitle()
        LoginInputContent(
            viewState = viewState,
            usernameChange = {
                viewModel.dispatch(LoginViewIntent.ChangeUsername(it))
            },
            passwordChange = {
                viewModel.dispatch(LoginViewIntent.ChangePassword(it))
            },
            doneChange = {
                focusManager.clearFocus()
                viewModel.dispatch(LoginViewIntent.RequestLogin)
            }
        )
        LoginFooter(viewState = viewState, gotoRegisterClick = {
            imeInsets.hasBottomExpend({ focusManager.clearFocus() }, {
                navController.navigateArgs(RoutePage.Account.Register.route)
            })
        }, loginClick = {
                focusManager.clearFocus()
                viewModel.dispatch(LoginViewIntent.RequestLogin)
            })
    }
}

@Composable
private fun LoginTitle() {
    Text(
        text = stringResource(id = R.string.account_login_title),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = ColorsTheme.colors.accent,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    )
}

@Composable
private fun LoginInputContent(
    viewState: LoginViewState,
    usernameChange: (String) -> Unit,
    passwordChange: (String) -> Unit,
    doneChange: KeyboardActionScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = ColorsTheme.colors.item),
        shape = RoundedCornerShape(OffsetRadiusMedium),
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
                }
            )
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
            .padding(horizontal = OffsetLarge)
            .fillMaxWidth()
            .padding(horizontal = OffsetLarge)
    ) {
        Text(
            text = stringResource(id = R.string.account_go_to_register_text),
            color = ColorsTheme.colors.focus,
            fontSize = FontSizeTheme.sizes.medium,
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
                containerColor =
                if (viewState.isLoginEnable) ColorsTheme.colors.focus else ButtonLockColor
            )
        ) {
            Text(
                text = stringResource(id = R.string.account_login_button),
                color = ButtonTextColor
            )
        }
    }
}