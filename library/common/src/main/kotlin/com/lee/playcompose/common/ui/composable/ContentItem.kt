package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.getAuthor
import com.lee.playcompose.common.ktx.getCategory
import com.lee.playcompose.common.ktx.getDateFormat
import com.lee.playcompose.common.ktx.getTitle
import com.lee.playcompose.common.ui.theme.*

/**
 * 各模块通用内容item组件
 * @param item [Content]数据实体
 * @param onItemClick item点击执行函数
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
                color = ColorsTheme.colors.accent,
                fontSize = FontSizeTheme.sizes.medium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(author) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
            )

            Text(
                text = item.getTitle(),
                color = ColorsTheme.colors.primary,
                fontSize = FontSizeTheme.sizes.small,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .padding(top = OffsetMedium, bottom = OffsetMedium)
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(author.bottom)
                    }
            )

            Text(
                text = item.getCategory(),
                color = ColorsTheme.colors.focus,
                fontSize = FontSizeTheme.sizes.smallX,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .constrainAs(category) {
                        start.linkTo(parent.start)
                        end.linkTo(time.start, OffsetMedium)
                        top.linkTo(title.bottom)
                        width = Dimension.fillToConstraints
                    }
            )

            Text(
                text = item.getDateFormat(),
                color = ColorsTheme.colors.primaryDark,
                fontSize = FontSizeTheme.sizes.smallX,
                modifier = Modifier
                    .constrainAs(time) {
                        end.linkTo(parent.end)
                        top.linkTo(category.top)
                        bottom.linkTo(category.bottom)
                    }
            )
        }
    }
}