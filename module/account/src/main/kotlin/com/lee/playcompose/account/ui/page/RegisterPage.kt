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
import androidx.compose.ui.ExperimentalComposeUiApi
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

/**
 * 用户注册页
 * @author jv.lee
 * @date 2022/3/23
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterPage(
    navController: NavController = LocalNavController.current,
    viewModel: RegisterViewModel = viewModel(),
    accountViewModel: AccountViewModel = activityViewModel()
) {
    val imeInsets = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.ime)
    val imePadding = rememberImePaddingValue(imeInsets = imeInsets)
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = viewModel.viewStates

    // 页面单向事件监听
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is RegisterViewEvent.RegisterSuccess -> {
                    accountViewModel.dispatch(
                        AccountViewAction.UpdateAccountStatus(event.accountData, true)
                    )
                    keyboardController?.hide()
                    // 通知login页面注册成功销毁页面
                    navController.setResult(REQUEST_KEY_LOGIN)
                    navController.popBackStack()
                }
                is RegisterViewEvent.RegisterFailed -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .fillMaxSize()
            .padding(bottom = imePadding.dp)
            .wrapContentSize(Alignment.Center)
            .onTap { keyboardController?.hide() })
    {
        RegisterTitle()
        RegisterInputContent(viewState = viewState, usernameChange = {
            viewModel.dispatch(RegisterViewAction.ChangeUsername(it))
        }, passwordChange = {
            viewModel.dispatch(RegisterViewAction.ChangePassword(it))
        }, rePasswordChange = {
            viewModel.dispatch(RegisterViewAction.ChangeRePassword(it))
        }, doneChange = {
            keyboardController?.hide()
            viewModel.dispatch(RegisterViewAction.RequestRegister)
        })

        RegisterFooter(viewState = viewState, gotoLoginClick = {
            imeInsets.hasBottomExpend({ keyboardController?.hide() }, {
                navController.popBackStack()
            })
        }, registerClick = {
            keyboardController?.hide()
            viewModel.dispatch(RegisterViewAction.RequestRegister)
        })
    }
}

@Composable
private fun RegisterTitle() {
    Text(
        text = stringResource(id = R.string.account_register_title),
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
private fun RegisterInputContent(
    viewState: RegisterViewState,
    usernameChange: (String) -> Unit,
    passwordChange: (String) -> Unit,
    rePasswordChange: (String) -> Unit,
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.vector_password),
                        contentDescription = null,
                        tint = ButtonLockColor
                    )
                })
            AccountSpacer()
            AppTextField(
                value = viewState.rePassword,
                onValueChange = rePasswordChange,
                hintText = stringResource(id = R.string.account_re_password_text),
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
private fun RegisterFooter(
    viewState: RegisterViewState,
    gotoLoginClick: () -> Unit,
    registerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Text(
            text = stringResource(id = R.string.account_go_to_login_text),
            color = AppTheme.colors.focus,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable {
                    gotoLoginClick()
                }
        )
        Button(
            onClick = registerClick,
            enabled = viewState.isRegisterEnable,
            shape = RoundedCornerShape(OffsetRadiusMedium),
            modifier = Modifier.align(Alignment.CenterEnd),
            colors = ButtonDefaults.buttonColors(
                backgroundColor =
                if (viewState.isRegisterEnable) AppTheme.colors.focus else ButtonLockColor
            ),
        ) {
            Text(
                text = stringResource(id = R.string.account_register_button),
                color = ButtonTextColor
            )
        }
    }
}