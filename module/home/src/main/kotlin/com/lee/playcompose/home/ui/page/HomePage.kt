package com.lee.playcompose.home.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.RefreshList
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

    val homeContent = viewState.pagingData.collectAsLazyPagingItems()
    val homeBanner = viewState.banners
    val homeCategory = viewState.category
    val isRefreshing = viewState.isRefreshing

    RefreshList(lazyPagingItems = homeContent, isRefreshing = isRefreshing, onRefresh = {
        viewModel.dispatch(HomeViewAction.RequestData)
    }) {
        // build home banner item
        if (homeBanner.isNotEmpty()) {

        }

        // build home category item
        if (homeCategory.isNotEmpty()) {

        }

        // build home content item
        itemsIndexed(homeContent) { _, item ->
            item ?: return@itemsIndexed
            HomeContentItem(item)
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun HomeContentItem(item: Content) {
    val titleText = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    val authorText = item.author.ifEmpty { item.shareUser }
    val timeText = item.niceShareDate
    val categoryText = item.chapterName

    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (author, title, category, time) = createRefs()

            Text(
                text = authorText,
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
                text = titleText,
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
                text = categoryText,
                color = AppTheme.colors.focus,
                fontSize = FontSizeSmallX,
                modifier = Modifier
                    .padding(top = OffsetMedium)
                    .constrainAs(category) {
                        start.linkTo(parent.start)
                        top.linkTo(title.bottom)
                    })

            Text(
                text = timeText,
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
