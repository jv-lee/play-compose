package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsHeight

/**
 * @author jv.lee
 * @date 2022/2/21
 * @description
 */
@Composable
fun AppBarContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
        )
        content()
    }
}