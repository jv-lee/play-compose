package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium

@Composable
fun LoadingDialog(isShow: Boolean) {
    if (isShow) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .size(width = 100.dp, height = 78.dp)
                    .background(
                        color = AppTheme.colors.item,
                        shape = RoundedCornerShape(OffsetRadiusMedium)
                    )
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
        }
    }
}