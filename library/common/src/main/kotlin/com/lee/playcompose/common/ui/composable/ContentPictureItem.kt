package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ktx.getCategory
import com.lee.playcompose.common.ktx.getDateFormat
import com.lee.playcompose.common.ktx.getDescription
import com.lee.playcompose.common.ktx.getTitle
import com.lee.playcompose.common.ui.theme.*

/**
 * 各模块通用内容item图片样式组件
 * @param item [Content]数据实体
 * @param onItemClick item点击执行函数
 * @author jv.lee
 * @date 2022/3/14
 */
@Composable
fun ContentPictureItem(
    item: Content,
    onItemClick: (Content) -> Unit
) {
    CardItemContainer(contentPadding = 0.dp, onClick = { onItemClick(item) }) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(146.dp)
        ) {
            val (picture, title, desc, category, time) = createRefs()

            Image(
                painter = rememberImagePainter(data = item.envelopePic),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(86.dp)
                    .fillMaxHeight()
                    .constrainAs(picture) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )

            Text(
                text = item.getTitle(),
                color = ColorsTheme.colors.accent,
                fontSize = FontSizeTheme.sizes.medium,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(picture.end, OffsetMedium)
                        top.linkTo(parent.top, OffsetMedium)
                        end.linkTo(parent.end, OffsetMedium)
                        width = Dimension.fillToConstraints
                    }
            )

            Text(
                text = item.getDescription(),
                color = ColorsTheme.colors.primary,
                fontSize = FontSizeTheme.sizes.small,
                overflow = TextOverflow.Ellipsis,
                maxLines = 5,
                modifier = Modifier
                    .constrainAs(desc) {
                        start.linkTo(picture.end, OffsetMedium)
                        top.linkTo(title.bottom, OffsetMedium)
                        end.linkTo(parent.end, OffsetMedium)
                        width = Dimension.fillToConstraints
                    }
            )

            Text(
                text = item.getCategory(),
                color = ColorsTheme.colors.focus,
                fontSize = FontSizeTheme.sizes.smallX,
                modifier = Modifier
                    .padding(top = OffsetMedium)
                    .constrainAs(category) {
                        start.linkTo(picture.end, OffsetMedium)
                        bottom.linkTo(parent.bottom, OffsetMedium)
                    }
            )

            Text(
                text = item.getDateFormat(),
                color = ColorsTheme.colors.primaryDark,
                fontSize = FontSizeTheme.sizes.smallX,
                modifier = Modifier
                    .constrainAs(time) {
                        end.linkTo(parent.end, OffsetMedium)
                        bottom.linkTo(parent.bottom, OffsetMedium)
                    }
            )
        }
    }
}