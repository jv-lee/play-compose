package com.lee.playcompose.system.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.base.tools.WeakDataHolder
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.ktx.formHtmlLabels
import com.lee.playcompose.common.ui.callback.PageCallbackHandler
import com.lee.playcompose.common.ui.composable.CardItemContainer
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.RouteParamsKey
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.system.R
import com.lee.playcompose.system.ui.callback.SystemCallback
import com.lee.playcompose.system.viewmodel.SystemContentViewModel
import com.lee.playcompose.system.viewmodel.SystemContentViewState
import kotlinx.coroutines.launch

const val systemCallbackKey = "systemCallbackKey"

/**
 * 体系页第一个tab 体系内容
 * @author jv.lee
 * @date 2022/3/8
 */
@Composable
fun SystemContentPage(
    handler: PageCallbackHandler<SystemCallback>,
    navController: NavController = LocalNavController.current,
    viewModel: SystemContentViewModel = viewModel()
) {
    val coroutine = rememberCoroutineScope()
    val viewState = viewModel.viewStates

    handler.addCallback(
        systemCallbackKey,
        object : SystemCallback {
            override fun tabChange() {
                coroutine.launch { viewState.listState.animateScrollToItem(0) }
            }
        }
    )

    SystemContentList(viewState = viewState, onItemClick = {
        // tabData跳转数据暂存 to SystemContentTabPage
        WeakDataHolder.instance.saveData(RouteParamsKey.tabDataKey, it)
        navController.navigateArgs(RoutePage.System.SystemContentTab.route)
    })
}

@Composable
private fun SystemContentList(
    viewState: SystemContentViewState,
    onItemClick: (ParentTab) -> Unit
) {
    val contentList = viewState.savedPager.getLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        lazyPagingItems = contentList,
        listState = listState,
        navigationPadding = true,
        swipeEnable = false
    ) {
        // header spacer
        item { HeaderSpacer() }

        // build system content item
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
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
                color = ColorsTheme.colors.accent,
                fontSize = FontSizeTheme.sizes.medium,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OffsetMedium + OffsetMedium + 1.dp)
                    .padding(top = OffsetMedium, bottom = OffsetMedium)
                    .background(ColorsTheme.colors.background)
            )
            Text(
                text = item.formHtmlLabels(),
                color = ColorsTheme.colors.primary,
                fontSize = FontSizeTheme.sizes.small
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OffsetMedium + OffsetMedium + 1.dp)
                    .padding(top = OffsetMedium, bottom = OffsetMedium)
                    .background(ColorsTheme.colors.background)
            )
            Text(
                text = stringResource(id = R.string.item_more),
                color = ColorsTheme.colors.focus,
                fontSize = FontSizeTheme.sizes.smallX,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.CenterEnd)
            )
        }
    }
}
