package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.R
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.getDateFormat
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.SlidingPaneBox
import com.lee.playcompose.common.ui.widget.SlidingPaneState

/**
 * 支持删除动作的文本item
 * @param item [Content] 数据实体
 * @param state slidingPane侧滑菜单状态
 * @param onItemClick item点击事件执行函数
 * @param onItemDelete item删除事件执行函数
 * @author jv.lee
 * @date 2022/3/29
 */
@Composable
fun ActionTextItem(
    item: Content,
    state: SlidingPaneState = SlidingPaneState(),
    onItemClick: (Content) -> Unit,
    onItemDelete: (Content) -> Unit
) {
    SlidingPaneBox(
        modifier = Modifier
            .height(76.dp)
            .fillMaxWidth()
            .padding(bottom = OffsetMedium), // margin
        state = state,
        sliding = {
            Text(
                text = stringResource(id = R.string.item_delete),
                fontSize = FontSizeTheme.sizes.small,
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        state.closeAction()
                        onItemDelete(item)
                    }
                    .background(Color.Red)
                    .wrapContentSize(Alignment.Center)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onItemClick(item) }
                .background(ColorsTheme.colors.item) // center
                .padding(OffsetLarge) // padding
        ) {
            Text(
                text = item.title,
                fontSize = FontSizeTheme.sizes.small,
                fontWeight = FontWeight.Bold,
                color = ColorsTheme.colors.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = item.getDateFormat(),
                fontSize = FontSizeTheme.sizes.smallX,
                color = ColorsTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}