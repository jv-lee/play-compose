package com.lee.playcompose.common.ui.widget.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lee.playcompose.common.extensions.createAppHeaderGradient
import com.lee.playcompose.common.ui.theme.ColorsTheme

/**
 * 公共appbar容器组件
 * @param modifier 属性配置
 * @param headerBrush 背景是否使用模糊渐变背景效果
 * @param content 内容content composable组件
 * @author jv.lee
 * @date 2022/2/21
 */
@Composable
fun AppHeaderContainer(
    modifier: Modifier = Modifier,
    headerBrush: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var mModifier = modifier
    if (headerBrush) {
        val appHeaderBrush = createAppHeaderGradient(
            ColorsTheme.colors.background,
            ColorsTheme.colors.backgroundTransparent
        )
        mModifier = modifier.background(brush = appHeaderBrush)
    }

    Column(mModifier) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
        content()
    }
}