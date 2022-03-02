package com.lee.playcompose.home.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.px2dp
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.getAuthor
import com.lee.playcompose.common.extensions.getCategory
import com.lee.playcompose.common.extensions.getDateFormat
import com.lee.playcompose.common.extensions.getTitle
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.home.model.entity.HomeCategory
import com.lee.playcompose.home.viewmodel.HomeViewAction
import com.lee.playcompose.home.viewmodel.HomeViewModel

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun HomePage(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val viewState = viewModel.viewStates

    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val bannerList = viewState.banners
    val categoryList = viewState.category
    val isRefreshing = viewState.isRefreshing

    RefreshList(lazyPagingItems = contentList, isRefreshing = isRefreshing, onRefresh = {
        viewModel.dispatch(HomeViewAction.RequestData)
    }) {
        // build home banner item
        if (bannerList.isNotEmpty()) {

        }

        // build home category item
        if (categoryList.isNotEmpty()) {
            item {
                HomeCategoryItem(categoryList)
            }
        }

        // build home content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            HomeContentItem(item)
        }
    }
}

@Composable
private fun HomeCategoryItem(categoryList: List<HomeCategory>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(categoryList) { item ->
            HomeCategoryChildItem(category = item)
        }
    }
}

@Composable
private fun HomeCategoryChildItem(category: HomeCategory) {
    val screenWidth = app.resources.displayMetrics.widthPixels
    val viewWidth = app.px2dp(screenWidth / 2)
    Box(
        modifier = Modifier
            .width(viewWidth.dp)
            .padding(OffsetMedium)
    ) {
        Card(backgroundColor = AppTheme.colors.item) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(OffsetLarge)
                    .wrapContentSize(align = Alignment.Center)
            ) {
                Image(
                    painter = painterResource(id = category.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(OffsetMedium))
                Text(text = category.name, color = AppTheme.colors.accent)
            }
        }
    }
}

@Composable
private fun HomeContentItem(item: Content) {
    Box(modifier = Modifier.padding(OffsetMedium)) {
        Card(backgroundColor = AppTheme.colors.item) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(OffsetLarge)
            ) {
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
}
