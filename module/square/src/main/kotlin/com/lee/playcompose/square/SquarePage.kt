package com.lee.playcompose.square

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.ContentItem
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.AppBarContainer
import com.lee.playcompose.common.ui.widget.AppGradientTextBar
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.square.viewmodel.SquareViewModel
import com.lee.playcompose.square.viewmodel.SquareViewState

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun SquarePage(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: SquareViewModel = viewModel()
) {
    Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
        // content
        SquareContentList(
            viewModel.viewStates,
            onContentItemClick = {
                toast(it.title)
            }
        )

        // header
        AppBarContainer {
            AppGradientTextBar(
                title = stringResource(id = R.string.square_title),
                navigationPainter = painterResource(id = R.drawable.vector_add),
                onNavigationClick = { toast("add click") }
            )
        }
    }
}

@Composable
fun SquareContentList(
    viewState: SquareViewState,
    onContentItemClick: (Content) -> Unit
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
        indicatorPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.statusBars,
            applyTop = true,
            additionalTop = ToolBarHeight
        )
    ) {
        // header spacer
        item {
            Spacer(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(ToolBarHeight)
            )
        }

        // build home content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            ContentItem(item, onContentItemClick)
        }
    }
}
