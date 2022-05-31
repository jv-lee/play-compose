package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.getAuthor
import com.lee.playcompose.common.extensions.getCategory
import com.lee.playcompose.common.extensions.getDateFormat
import com.lee.playcompose.common.extensions.getTitle
import com.lee.playcompose.common.ui.theme.*

/**
 * 各模块通用内容item组件
 * @author jv.lee
 * @date 2022/3/3
 */

@Composable
fun ContentItem(
    item: Content,
    onItemClick: (Content) -> Unit
) {
    CardItemContainer(onClick = { onItemClick(item) }) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (author, title, category, time) = createRefs()

            Text(
                text = item.getAuthor(),
                color = AppTheme.colors.accent,
                fontSize = FontSizeMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(author) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
            )

            Text(
                text = item.getTitle(),
                color = AppTheme.colors.primary,
                fontSize = FontSizeSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .padding(top = OffsetMedium)
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(author.bottom)
                    }
            )

            Text(
                text = item.getCategory(),
                color = AppTheme.colors.focus,
                fontSize = FontSizeSmallX,
                modifier = Modifier
                    .padding(top = OffsetMedium)
                    .constrainAs(category) {
                        start.linkTo(parent.start)
                        top.linkTo(title.bottom)
                    })

            Text(
                text = item.getDateFormat(),
                color = AppTheme.colors.primaryDark,
                fontSize = FontSizeSmallX,
                modifier = Modifier
                    .constrainAs(time) {
                        end.linkTo(parent.end)
                        top.linkTo(category.top)
                        bottom.linkTo(category.bottom)
                    })
        }
    }
}