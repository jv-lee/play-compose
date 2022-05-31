package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.OnLifecycleEvent
import com.lee.playcompose.base.extensions.activityViewModel
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.common.entity.CoinRecord
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.ActionMode
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.viewmodel.ThemeViewModel
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.CoinViewModel
import com.lee.playcompose.me.viewmodel.CoinViewState
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.common.R as CR

/**
 * 个人积分页
 * @author jv.lee
 * @date 2022/3/25
 */
@Composable
fun CoinPage(
    navController: NavController,
    viewModel: CoinViewModel = viewModel(),
    themeViewModel: ThemeViewModel = activityViewModel(),
    systemUiController: SystemUiController = rememberSystemUiController()
) {
    val viewState = viewModel.viewStates
    val themeState = themeViewModel.viewStates
    val accountViewState = viewModel.accountService.getAccountViewStates(LocalActivity.current)

    OnLifecycleEvent(onEvent = { event ->
        if (event == Lifecycle.Event.ON_START) {
            systemUiController.statusBarDarkContentEnabled = false
        }
        if (event == Lifecycle.Event.ON_STOP) {
            systemUiController.statusBarDarkContentEnabled = themeState.statusBarDarkContentEnabled
        }
    })

    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_coin),
        elevation = 0.dp,
        actionIcon = R.drawable.vector_help,
        actionMode = ActionMode.Button,
        backgroundColor = AppTheme.colors.focus,
        contentColor = Color.White,
        navigationClick = { navController.popBackStack() },
        actionClick = { navController.navigateArgs(RoutePage.Details.route, viewState.detailsData) }
    ) {
        Box {
            Box(modifier = Modifier.padding(top = 182.dp)) {
                CoinRecordContent(viewState = viewState)
            }
            CoinRecordHeader(accountViewState = accountViewState, coinRankClick = {
                navController.navigateArgs(RoutePage.Me.CoinRank.route)
            })
        }
    }
}

@Composable
private fun CoinRecordContent(viewState: CoinViewState) {
    val contentList = viewState.savedPager.getLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        swipeEnable = false,
        lazyPagingItems = contentList,
        listState = listState,
    ) {
        // build coinRecord content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            CoinRecordItem(item = item)
        }
    }
}

@Composable
private fun CoinRecordItem(item: CoinRecord) {
    Text(
        text = item.desc,
        fontSize = FontSizeSmall,
        color = AppTheme.colors.accent,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = OffsetMedium)
            .background(AppTheme.colors.item)
            .padding(OffsetLarge)
    )
}

@Composable
private fun CoinRecordHeader(accountViewState: AccountViewState, coinRankClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .background(
                    color = AppTheme.colors.focus,
                    shape = RoundedCornerShape(
                        bottomStart = 56.dp,
                        bottomEnd = 56.dp
                    )
                )
        )

        Card(
            backgroundColor = AppTheme.colors.item,
            shape = RoundedCornerShape(OffsetRadiusMedium),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = OffsetMedium,
                    start = OffsetLarge,
                    end = OffsetLarge
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.coin_title_label_text),
                    fontSize = FontSizeSmall,
                    color = AppTheme.colors.focus,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(26.dp)
                        .fillMaxWidth()
                        .background(
                            color = AppTheme.colors.onFocus,
                            shape = RoundedCornerShape(
                                topStart = OffsetRadiusMedium,
                                topEnd = OffsetRadiusMedium
                            )
                        )
                        .wrapContentHeight(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(id = R.string.coin_total_description),
                    fontSize = FontSizeSmallX,
                    color = AppTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = OffsetLarge)
                )
                Text(
                    text = accountViewState.accountData?.coinInfo?.coinCount.toString(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = OffsetLarge)
                )
                Spacer(
                    modifier = Modifier
                        .height(OffsetLarge + 1.dp)
                        .fillMaxWidth()
                        .padding(
                            start = OffsetLarge,
                            top = OffsetLarge,
                            end = OffsetLarge
                        )
                        .background(AppTheme.colors.onFocus)
                )
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .clickable { coinRankClick() }
                ) {
                    Text(
                        text = stringResource(id = R.string.coin_to_rank_text),
                        fontSize = FontSizeLarge,
                        color = AppTheme.colors.accent,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = OffsetLarge)
                    )
                    Icon(
                        painter = painterResource(id = CR.drawable.vector_arrow),
                        tint = AppTheme.colors.accent,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = OffsetLarge)
                    )
                }
            }
        }
    }
}