package com.lee.playcompose.splash

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.R
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
import com.lee.playcompose.service.AccountService
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/3/4
 * @description 闪屏页路由组件 闪屏切换主路由ui
 */
@Composable
fun SplashLauncher(content: @Composable () -> Unit) {
    var isSplash by remember { mutableStateOf(true) }
    if (isSplash) {
        SplashPage { isSplash = false }
    } else {
        content()
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun SplashPage(viewModel: SplashViewModel = viewModel(), onNextPage: () -> Unit) {
    val statusInsets =
        rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars)
    val navigationInsets =
        rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)
    val activity = LocalActivity.current
    val viewState = viewModel.viewStates

    LaunchedEffect(Unit) {
        // 同步获取账户配置
        ModuleService.find<AccountService>().requestAccountInfo(activity)

        // 加载splash逻辑
        viewModel.dispatch(SplashViewAction.StartTimeTask)
        viewModel.viewEvents.collect { event ->
            if (event is SplashViewEvent.NavigationMain) {
                onNextPage()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(navigationInsets)
            .wrapContentSize(align = Alignment.TopCenter)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.splash_ad),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
        )

        AnimatedVisibility(
            visible = viewState.timeVisible,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = statusInsets.calculateTopPadding() + OffsetSmall,
                    end = OffsetLarge
                )
        ) {
            Button(
                onClick = { viewModel.dispatch(SplashViewAction.NavigationMain) },
                shape = RoundedCornerShape(OffsetRadiusMedium),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x1a000000)),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(text = viewState.timeText, color = Color.White)
            }
        }
    }
}