package com.lee.playcompose.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun MainPage() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize(align = Alignment.Center)
        ) {
            Text(text = "Main Page.")
        }
    }

}