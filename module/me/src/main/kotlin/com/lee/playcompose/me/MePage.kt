package com.lee.playcompose.me

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun MePage(navController: NavController, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        Text(text = "Me Page")
    }
}