package com.lee.playcompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.service.AccountService
import kotlinx.coroutines.delay

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

@Composable
private fun SplashPage(onNextPage: () -> Unit) {
    val navigationInsets =
        rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)
    val activity = LocalActivity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 140.dp + navigationInsets.calculateBottomPadding())
            .wrapContentSize(align = Alignment.TopCenter)
    ) {
        LaunchedEffect(Unit) {
            ModuleService.find<AccountService>().requestAccountInfo(activity)
            delay(2000)
            onNextPage()
        }
        Image(
            painter = painterResource(id = R.mipmap.splash_ad),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        )
    }
}