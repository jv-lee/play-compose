package com.lee.playcompose.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun DetailsPage(navController: NavController) {

    Box(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = "Details Page")
    }
}