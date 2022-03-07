package com.lee.playcompose.system

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.lee.playcompose.router.PageRoute

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun SystemPage(navController: NavController, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomCenter)
    ) {
        Text(text = "System Page", Modifier.clickable {
            navController.navigate(PageRoute.Details.route)
        })
    }
}