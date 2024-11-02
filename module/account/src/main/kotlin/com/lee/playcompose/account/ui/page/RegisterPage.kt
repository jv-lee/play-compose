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

/**
 * 用户注册页
 * @author jv.lee
 * @date 2022/3/23
 */
@Composable
fun RegisterPage(
    navController: NavController = LocalNavController.current,
    viewModel: RegisterViewModel = viewModel(),
    accountViewModel: AccountViewModel = activityViewModel()
) {
    val imeInsets = WindowInsets.ime.asPaddingValues()
    val focusManager = LocalFocusManager.current
    val viewState = viewModel.viewStates()

    // 页面单向事件监听
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is RegisterViewEvent.RegisterSuccess -> {
                    accountViewModel.dispatch(
                        AccountViewIntent.UpdateAccountStatus(event.accountData, true)
                    )
                    focusManager.clearFocus()
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
            .background(ColorsTheme.colors.background)
            .onTap { focusManager.clearFocus() }
            .fillMaxSize()
            .imePadding()
            .wrapContentSize(Alignment.Center)
    ) {
        RegisterTitle()
        RegisterInputContent(
            viewState = viewState,
            usernameChange = {
                viewModel.dispatch(RegisterViewIntent.ChangeUsername(it))
            },
            passwordChange = {
                viewModel.dispatch(RegisterViewIntent.ChangePassword(it))
            },
            rePasswordChange = {
                viewModel.dispatch(RegisterViewIntent.ChangeRePassword(it))
            },
            doneChange = {
                focusManager.clearFocus()
                viewModel.dispatch(RegisterViewIntent.RequestRegister)
            }
        )

        RegisterFooter(viewState = viewState, gotoLoginClick = {
            imeInsets.hasBottomExpend({ focusManager.clearFocus() }, {
                navController.popBackStack()
            })
        }, registerClick = {
                focusManager.clearFocus()
                viewModel.dispatch(RegisterViewIntent.RequestRegister)
            })
    }
}

@Composable
private fun RegisterTitle() {
    Text(
        text = stringResource(id = R.string.account_register_title),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = ColorsTheme.colors.accent,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    )
}

@Composable
private fun RegisterInputContent(
    viewState: RegisterViewState,
    usernameChange: (String) -> Unit,
    passwordChange: (String) -> Unit,
    rePasswordChange: (String) -> Unit,
    doneChange: KeyboardActionScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = ColorsTheme.colors.item),
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
                }
            )
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
                }
            )
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
            .padding(horizontal = OffsetLarge)
            .fillMaxWidth()
            .padding(horizontal = OffsetLarge)
    ) {
        Text(
            text = stringResource(id = R.string.account_go_to_login_text),
            color = ColorsTheme.colors.focus,
            fontSize = FontSizeTheme.sizes.medium,
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
                containerColor =
                if (viewState.isRegisterEnable) ColorsTheme.colors.focus else ButtonLockColor
            )
        ) {
            Text(
                text = stringResource(id = R.string.account_register_button),
                color = ButtonTextColor
            )
        }
    }
}