package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.getCategory
import com.lee.playcompose.common.extensions.getDateFormat
import com.lee.playcompose.common.extensions.getDescription
import com.lee.playcompose.common.extensions.getTitle
import com.lee.playcompose.common.ui.theme.*

/**
 * @author jv.lee
 * @date 2022/3/14
 * @description
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
//fun ContentPictureItem(
//    item: Content,
//    onItemClick: (Content) -> Unit
//) {
//    CardItemContainer(onClick = { onItemClick(item) }) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(146.dp)
//        ) {
//            Row {
//                Image(
//                    painter = rememberImagePainter(data = item.envelopePic),
//                    contentDescription = null,
//                    contentScale = ContentScale.FillBounds,
//                    modifier = Modifier
//                        .width(86.dp)
//                        .fillMaxHeight()
//                )
//                ConstraintLayout(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxSize()
//                        .padding(OffsetMedium)
//                ) {
//                    val (title, desc, category, time) = createRefs()
//
//                    Text(
//                        text = item.getTitle(),
//                        color = AppTheme.colors.accent,
//                        fontSize = FontSizeMedium,
//                        fontWeight = FontWeight.Bold,
//                        overflow = TextOverflow.Ellipsis,
//                        maxLines = 1,
//                        modifier = Modifier
//                            .constrainAs(title) {
//                                start.linkTo(parent.start)
//                                top.linkTo(parent.top)
//                            }
//                    )
//
//                    Text(
//                        text = item.desc,
//                        color = AppTheme.colors.primary,
//                        fontSize = FontSizeSmall,
//                        overflow = TextOverflow.Ellipsis,
//                        maxLines = 5,
//                        modifier = Modifier
//                            .padding(top = OffsetMedium)
//                            .constrainAs(desc) {
//                                start.linkTo(parent.start)
//                                top.linkTo(title.bottom)
//                            }
//                    )
//
//                    Text(
//                        text = item.getCategory(),
//                        color = AppTheme.colors.focus,
//                        fontSize = FontSizeSmallX,
//                        modifier = Modifier
//                            .padding(top = OffsetMedium)
//                            .constrainAs(category) {
//                                start.linkTo(parent.start)
//                                bottom.linkTo(parent.bottom)
//                            })
//
//                    Text(
//                        text = item.getDateFormat(),
//                        color = AppTheme.colors.primaryDark,
//                        fontSize = FontSizeSmallX,
//                        modifier = Modifier
//                            .constrainAs(time) {
//                                end.linkTo(parent.end)
//                                bottom.linkTo(parent.bottom)
//                            })
//                }
//            }
//        }
//    }
//}
fun ContentPictureItem(
    item: Content,
    onItemClick: (Content) -> Unit
) {
    CardItemContainer(onClick = { onItemClick(item) }) {
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
                color = AppTheme.colors.accent,
                fontSize = FontSizeMedium,
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
                color = AppTheme.colors.primary,
                fontSize = FontSizeSmall,
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
                color = AppTheme.colors.focus,
                fontSize = FontSizeSmallX,
                modifier = Modifier
                    .padding(top = OffsetMedium)
                    .constrainAs(category) {
                        start.linkTo(picture.end, OffsetMedium)
                        bottom.linkTo(parent.bottom, OffsetMedium)
                    })

            Text(
                text = item.getDateFormat(),
                color = AppTheme.colors.primaryDark,
                fontSize = FontSizeSmallX,
                modifier = Modifier
                    .constrainAs(time) {
                        end.linkTo(parent.end, OffsetMedium)
                        bottom.linkTo(parent.bottom, OffsetMedium)
                    })
        }
    }
}