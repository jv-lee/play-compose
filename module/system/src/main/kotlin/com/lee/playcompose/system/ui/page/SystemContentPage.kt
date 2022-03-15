package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.base.tools.WeakDataHolder
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.entity.Tab
import com.lee.playcompose.common.ui.composable.CardItemContainer
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.router.ParamsKey
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.system.R
import com.lee.playcompose.system.viewmodel.SystemContentViewModel
import com.lee.playcompose.system.viewmodel.SystemContentViewState

/**
 * @author jv.lee
 * @date 2022/3/8
 * @description 体系页第一个tab 体系内容
 */
@Composable
fun SystemContentPage(
    navController: NavController,
    viewModel: SystemContentViewModel = viewModel()
) {
    SystemContentList(viewState = viewModel.viewStates, onItemClick = {
        WeakDataHolder.instance.saveData(ParamsKey.tabDataKey, it)
        navController.navigateArgs(PageRoute.SystemContentTab.route)
    })
}

@Composable
private fun SystemContentList(
    viewState: SystemContentViewState,
    onItemClick: (ParentTab) -> Unit
) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
        swipeEnable = false,
    ) {
        // header spacer
        item { HeaderSpacer() }

        // build system content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            SystemContentItem(item = item, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun SystemContentItem(item: ParentTab, onItemClick: (ParentTab) -> Unit) {
    CardItemContainer(onClick = { onItemClick(item) }) {
        Column {
            Text(
                text = item.name,
                color = AppTheme.colors.accent,
                fontSize = FontSizeMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(OffsetLarge)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppTheme.colors.background)
            )
            Text(
                text = formHtmlLabels(item),
                color = AppTheme.colors.primary,
                fontSize = FontSizeSmall,
                modifier = Modifier.padding(OffsetMedium)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppTheme.colors.background)
            )
            Text(
                text = stringResource(id = R.string.item_more),
                color = AppTheme.colors.focus,
                fontSize = FontSizeSmallX,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(OffsetLarge)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.CenterEnd)
            )
        }
    }
}

private fun formHtmlLabels(item: ParentTab): String {
    return HtmlCompat.fromHtml(
        buildChildrenLabel(item.children),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
}

private fun buildChildrenLabel(tabs: List<Tab>): String {
    val builder = StringBuilder()
    tabs.forEach {
        builder.append(it.name + "\t\t")
    }
    return builder.toString()
}

