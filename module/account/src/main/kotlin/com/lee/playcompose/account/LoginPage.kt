package com.lee.playcompose.account

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.account.ui.theme.ButtonLockColor
import com.lee.playcompose.account.ui.theme.ButtonTextColor
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium

/**
 * @author jv.lee
 * @date 2022/3/23
 * @description
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(rememberInsetsPaddingValues(insets = LocalWindowInsets.current.ime))
            .wrapContentSize(Alignment.Center)
            .background(AppTheme.colors.background)
    ) {
        Column {
            Text(
                text = "Login",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(OffsetLarge)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(OffsetLarge)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AppTextField(
                        value = "",
                        onValueChange = {},
                        hintText = "username",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.vector_username),
                                contentDescription = null,
                                tint = ButtonLockColor
                            )
                        }
                    )
                    AppTextField(
                        value = "",
                        onValueChange = {},
                        hintText = "password",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.vector_password),
                                contentDescription = null,
                                tint = ButtonLockColor
                            )
                        })
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(OffsetLarge)
            ) {
                Text(
                    text = "没有账号，去注册？",
                    color = AppTheme.colors.focus,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonLockColor),
                    shape = RoundedCornerShape(OffsetRadiusMedium),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(text = "LOGIN", color = ButtonTextColor)
                }
            }
        }
    }
}