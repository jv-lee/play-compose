package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall

/**
 * 卡片item容器组件
 * @param contentPadding 内容padding值
 * @param onClick 点击事件执行函数
 * @param content 内部渲染compose组件
 * @author jv.lee
 * @date 2022/3/8
 */
@Composable
fun CardItemContainer(
    modifier: Modifier = Modifier,
    contentPadding: Dp = OffsetLarge,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        backgroundColor = ColorsTheme.colors.item,
        modifier = modifier.padding(
            start = OffsetMedium,
            end = OffsetMedium,
            top = OffsetMedium,
            bottom = OffsetSmall
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(contentPadding)
        ) {
            content()
        }
    }
}