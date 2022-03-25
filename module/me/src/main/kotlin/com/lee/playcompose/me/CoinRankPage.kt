package com.lee.playcompose.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.lee.playcompose.common.ui.theme.AppTheme

/**
 * @author jv.lee
 * @date 2022/3/25
 * @description
 */
@Composable
fun CoinRankPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    )
}