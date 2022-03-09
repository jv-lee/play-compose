package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import com.lee.playcompose.common.ui.theme.ToolBarHeight

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 通用主页头部占位组件
 */
@Composable
fun HeaderSpacer() {
    Spacer(
        modifier = Modifier
            .statusBarsPadding()
            .height(ToolBarHeight)
    )
}