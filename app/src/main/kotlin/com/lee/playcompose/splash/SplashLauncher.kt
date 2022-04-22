package com.lee.playcompose.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsPadding
import com.lee.playcompose.R
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/3/4
 * @description 闪屏页路由组件 闪屏切换主路由ui
 */
@Composable
fun SplashLauncher(viewModel: SplashViewModel = viewModel(), content: @Composable () -> Unit) {
    val viewState = viewModel.viewStates
    AnimatedVisibility(
        visible = viewState.splashVisible,
        enter = fadeIn(animationSpec = TweenSpec(600)),
        exit = fadeOut(animationSpec = TweenSpec(600))
    ) {
        SplashPage(viewModel)
    }

    AnimatedVisibility(
        visible = viewState.contentVisible,
        enter = fadeIn(animationSpec = TweenSpec(600)),
        exit = fadeOut(animationSpec = TweenSpec(600))
    ) {
        content()
    }
}

@Composable
private fun SplashPage(viewModel: SplashViewModel) {
    val activity = LocalActivity.current
    val viewState = viewModel.viewStates

    LaunchedEffect(Unit) {
        // 同步获取账户配置
        ModuleService.find<AccountService>().requestAccountInfo(activity)

        // 加载splashAd逻辑
        viewModel.dispatch(SplashViewAction.RequestSplashAd)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.window)
            .padding(bottom = 86.dp)
    ) {
        Image(
            painter = painterResource(id = CR.mipmap.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Image(
            painter = painterResource(id = viewState.splashInfoRes),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        )
        SplashAdView(viewState = viewState, onNextClick = {
            viewModel.dispatch(SplashViewAction.NavigationMain)
        })
    }
}

@Composable
private fun SplashAdView(viewState: SplashViewState, onNextClick: () -> Unit) {
    AnimatedVisibility(
        visible = viewState.splashAdVisible,
        enter = fadeIn(animationSpec = TweenSpec(600)),
        exit = fadeOut(animationSpec = TweenSpec(600))
    ) {
        Box {
            Image(
                painter = painterResource(id = R.mipmap.splash_ad),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
            )
            Button(
                onClick = { onNextClick() },
                shape = RoundedCornerShape(OffsetRadiusMedium),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x1a000000)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = OffsetSmall, end = OffsetLarge)
            ) {
                Text(text = viewState.timeText, color = Color.White)
            }
        }
    }
}