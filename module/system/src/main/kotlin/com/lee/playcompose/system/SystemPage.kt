package com.lee.playcompose.system

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.lee.playcompose.router.PageRoute

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun SystemPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomCenter)
    ) {
        Text(text = "System Page", Modifier.clickable {
            navController.navigate(PageRoute.Details.route)
        })
    }
}