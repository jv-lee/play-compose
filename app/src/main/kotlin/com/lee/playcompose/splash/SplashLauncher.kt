package com.lee.playcompose.splash

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lee.playcompose.R
import com.lee.playcompose.base.extensions.FadeAnimatedVisibility
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService

/**
 * 闪屏页路由组件 闪屏切换主路由ui
 * @author jv.lee
 * @date 2022/3/4
 */
@Composable
fun SplashLauncher(viewModel: SplashViewModel = viewModel(), content: @Composable () -> Unit) {
    val viewState = viewModel.viewStates()

    // 主页内容
    FadeAnimatedVisibility(visible = viewState.contentVisible) {
        content()
    }

    // 闪屏内容
    FadeAnimatedVisibility(visible = viewState.splashVisible) {
        SplashPage(viewModel)
    }
}

@Composable
private fun SplashPage(viewModel: SplashViewModel) {
    val activity = LocalActivity.current
    val viewState = viewModel.viewStates()

    LaunchedEffect(Unit) {
        // 同步获取账户配置
        ModuleService.find<AccountService>().requestAccountInfo(activity)

        // 配置获取成功后启动主页构建
        viewModel.dispatch(SplashViewIntent.ShowContent)

        // 加载splashAd逻辑
        viewModel.dispatch(SplashViewIntent.RequestSplashAd)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ColorsTheme.colors.window)
    ) {
        Image(
            painter = painterResource(id = viewState.splashLogoRes),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Image(
            painter = painterResource(id = viewState.splashInfoRes),
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(bottom = 86.dp)
        )
        SplashAdView(viewState = viewState, onNextClick = {
            viewModel.dispatch(SplashViewIntent.HideSplash)
        })
    }
}

@Composable
private fun SplashAdView(viewState: SplashViewState, onNextClick: () -> Unit) {
    // 添加横屏时NavigationPadding值获取
    val navigationInsets: PaddingValues = WindowInsets.navigationBars.asPaddingValues()
    val orientation = LocalContext.current.resources.configuration.orientation
    val paddingEnd =
        if (orientation == Configuration.ORIENTATION_PORTRAIT) 0.dp
        else navigationInsets.calculateRightPadding(LayoutDirection.Ltr)
    FadeAnimatedVisibility(visible = viewState.splashAdVisible) {
        Box {
            Image(
                painter = painterResource(id = R.mipmap.splash_ad),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            )
            Button(
                onClick = { onNextClick() },
                shape = RoundedCornerShape(OffsetRadiusMedium),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x1a000000)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = OffsetSmall, end = OffsetLarge + paddingEnd)
                    .statusBarsPadding()
            ) {
                Text(text = viewState.timeText, color = Color.White)
            }
        }
    }
}