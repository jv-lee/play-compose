package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsHeight
import com.lee.playcompose.common.extensions.createAppHeaderGradient
import com.lee.playcompose.common.ui.theme.AppTheme

/**
 * @author jv.lee
 * @date 2022/2/21
 * @description 公共appbar容器组件
 */
@Composable
fun AppBarContainer(
    modifier: Modifier = Modifier,
    headerBrush: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var mModifier = modifier
    if (headerBrush) {
        val appHeaderBrush = createAppHeaderGradient(
            AppTheme.colors.background,
            AppTheme.colors.backgroundTransparent
        )
        mModifier = modifier.background(brush = appHeaderBrush)
    }

    Column(mModifier) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
        )
        content()
    }
}