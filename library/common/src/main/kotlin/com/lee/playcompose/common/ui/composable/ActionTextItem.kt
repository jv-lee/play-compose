package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.getDateFormat
import com.lee.playcompose.common.ui.theme.*

/**
 * @author jv.lee
 * @date 2022/3/29
 * @description
 */
@Composable
fun ActionTextItem(item: Content, onItemClick: (Content) -> Unit) {
    Box(
        modifier = Modifier
            .height(76.dp)
            .fillMaxWidth()
            .padding(bottom = OffsetMedium) // margin
            .clickable { onItemClick(item) }
            .background(AppTheme.colors.item)
            .padding(OffsetLarge) // padding
    ) {
        Text(
            text = item.title,
            fontSize = FontSizeSmall,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = item.getDateFormat(),
            fontSize = FontSizeSmallX,
            color = AppTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}