package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetMedium

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 卡片item容器组件
 */
@Composable
fun CardItemContainer(
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = Modifier.padding(OffsetMedium)) {
        Card(backgroundColor = AppTheme.colors.item) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick()
                }) {
                content()
            }
        }
    }
}