package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalActivity
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.base.ktx.OnLifecycleEvent
import com.lee.playcompose.base.ktx.activityViewModel
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.common.entity.CoinRecord
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.common.ui.widget.header.ActionMode
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
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
    navController: NavController = LocalNavController.current,
    viewModel: CoinViewModel = viewModel(),
    themeViewModel: ThemeViewModel = activityViewModel(),
) {
    val window = LocalActivity.current.window
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)

    val viewState = viewModel.viewStates
    val themeState = themeViewModel.viewStates
    val accountViewState = viewModel.accountService.getAccountViewStates(LocalActivity.current)

    OnLifecycleEvent(onEvent = { event ->
        if (event == Lifecycle.Event.ON_START) {
            insetsController.isAppearanceLightStatusBars = false
        }
        if (event == Lifecycle.Event.ON_STOP) {
            insetsController.isAppearanceLightStatusBars = themeState.statusBarDarkContentEnabled
        }
    })

    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_coin),
        actionIcon = R.drawable.vector_help,
        actionMode = ActionMode.Button,
        backgroundColor = ColorsTheme.colors.focus,
        contentColor = Color.White,
        navigationClick = { navController.popBackStack() },
        actionClick = {
            navController.navigateArgs(
                RoutePage.Details.route,
                viewState.detailsData
            )
        },
        appBarFooter = {
            CoinRecordHeader(accountViewState = accountViewState, coinRankClick = {
                navController.navigateArgs(RoutePage.Me.CoinRank.route)
            })
        },
        content = {
            CoinRecordContent(viewState = viewState)
        }
    )
}

@Composable
private fun CoinRecordContent(viewState: CoinViewState) {
    val contentList = viewState.savedPager.getLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    RefreshList(
        swipeEnable = false,
        lazyPagingItems = contentList,
        listState = listState
    ) {
        // build coinRecord content item
        items(contentList.itemCount) { index ->
            val item = contentList[index] ?: return@items
            CoinRecordItem(item = item)
        }
    }
}

@Composable
private fun CoinRecordItem(item: CoinRecord) {
    Text(
        text = item.desc,
        fontSize = FontSizeTheme.sizes.small,
        color = ColorsTheme.colors.accent,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = OffsetMedium)
            .background(ColorsTheme.colors.item)
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
                    color = ColorsTheme.colors.focus,
                    shape = RoundedCornerShape(
                        bottomStart = 56.dp,
                        bottomEnd = 56.dp
                    )
                )
        )

        Card(
            colors = CardDefaults.cardColors().copy(containerColor = ColorsTheme.colors.item),
            shape = RoundedCornerShape(OffsetRadiusMedium),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    start = OffsetLarge,
                    end = OffsetLarge
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.coin_title_label_text),
                    fontSize = FontSizeTheme.sizes.small,
                    color = ColorsTheme.colors.focus,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(26.dp)
                        .fillMaxWidth()
                        .background(
                            color = ColorsTheme.colors.onFocus,
                            shape = RoundedCornerShape(
                                topStart = OffsetRadiusMedium,
                                topEnd = OffsetRadiusMedium
                            )
                        )
                        .wrapContentHeight(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(id = R.string.coin_total_description),
                    fontSize = FontSizeTheme.sizes.smallX,
                    color = ColorsTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = OffsetLarge)
                )
                Text(
                    text = accountViewState.accountData?.coinInfo?.coinCount.toString(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorsTheme.colors.accent,
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
                        .background(ColorsTheme.colors.onFocus)
                )
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .clickable { coinRankClick() }
                ) {
                    Text(
                        text = stringResource(id = R.string.coin_to_rank_text),
                        fontSize = FontSizeTheme.sizes.medium,
                        color = ColorsTheme.colors.accent,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = OffsetLarge)
                    )
                    Icon(
                        painter = painterResource(id = CR.drawable.vector_arrow),
                        tint = ColorsTheme.colors.accent,
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