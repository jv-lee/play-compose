package com.lee.playcompose.account.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.account.R
import com.lee.playcompose.account.ui.composable.AccountSpacer
import com.lee.playcompose.account.ui.theme.ButtonLockColor
import com.lee.playcompose.account.ui.theme.ButtonTextColor
import com.lee.playcompose.base.extensions.onTap
import com.lee.playcompose.common.extensions.hasBottomExpend
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.router.PageRoute

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description
 */
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(navController: NavController) {
    val imeInsets = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.ime)
    val keyboardController = LocalSoftwareKeyboardController.current

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
            LoginInputContent()
            LoginFooter(gotoRegisterClick = {
                imeInsets.hasBottomExpend({ keyboardController?.hide() }, {
                    navController.navigate(PageRoute.Register.route)
                })
            }, loginClick = {

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
private fun LoginInputContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AppTextField(
                value = "",
                onValueChange = {},
                hintText = stringResource(id = R.string.account_username_text),
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
                value = "",
                onValueChange = {},
                hintText = stringResource(id = R.string.account_password_text),
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
private fun LoginFooter(gotoRegisterClick: () -> Unit, loginClick: () -> Unit) {
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
            onClick = { loginClick() },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonLockColor),
            shape = RoundedCornerShape(OffsetRadiusMedium),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = stringResource(id = R.string.account_login_button),
                color = ButtonTextColor
            )
        }
    }
}