package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.R
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.getDateFormat
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.SlidingPaneBox
import com.lee.playcompose.common.ui.widget.SlidingPaneState

/**
 * @author jv.lee
 * @date 2022/3/29
 * @description
 */
@Composable
fun ActionTextItem(
    item: Content,
    state: SlidingPaneState = SlidingPaneState(),
    onItemClick: (Content) -> Unit,
    onItemDelete: (Content) -> Unit,
) {
    SlidingPaneBox(modifier = Modifier
        .height(76.dp)
        .fillMaxWidth()
        .padding(bottom = OffsetMedium), // margin
        state = state,
        sliding = {
            Text(
                text = stringResource(id = R.string.item_delete),
                fontSize = FontSizeSmall,
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onItemDelete(item) }
                    .background(Color.Red)
                    .wrapContentSize(Alignment.Center)
            )
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onItemClick(item) }
                .background(AppTheme.colors.item) // center
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
}