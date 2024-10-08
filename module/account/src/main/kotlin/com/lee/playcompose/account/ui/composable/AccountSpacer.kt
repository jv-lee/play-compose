package com.lee.playcompose.account.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge

@Composable
fun AccountSpacer() {
    Box(modifier = Modifier.padding(start = OffsetLarge, end = OffsetLarge)) {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(ColorsTheme.colors.background)
        )
    }
}