package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.lee.playcompose.common.entity.CoinRank
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.ActionMode
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.RefreshList
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.CoinRankViewModel
import com.lee.playcompose.me.viewmodel.CoinRankViewState
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs

/**
 * 积分排行榜
 * @author jv.lee
 * @date 2022/3/25
 */
@Composable
fun CoinRankPage(navController: NavController, viewModel: CoinRankViewModel = viewModel()) {
    val viewState = viewModel.viewStates

    AppBarViewContainer(title = stringResource(id = R.string.coin_rank_title),
        actionIcon = R.drawable.vector_help,
        actionMode = ActionMode.Button,
        navigationClick = { navController.popBackStack() },
        actionClick = { navController.navigateArgs(RoutePage.Details.route, viewState.detailsData) }
    ) {
        CoinRankContent(viewState = viewState)
    }
}

@Composable
private fun CoinRankContent(viewState: CoinRankViewState) {
    val contentList = viewState.pagingData.collectAsLazyPagingItems()
    val listState = if (contentList.itemCount > 0) viewState.listState else LazyListState()

    // TODO 此处应该使用  LazyVerticalGrid实现，但是现阶段LazyVerticalGrid存在bug , 所以coinRank暂时不使用缓存方式获取数据
    RefreshList(
        lazyPagingItems = contentList,
        listState = listState
    ) {
        item { CoinRankTopHeader(data = viewState.topList) }

        // build coinRank content item
        itemsIndexed(contentList) { _, item ->
            item ?: return@itemsIndexed
            CoinRankItem(coinRank = item)
        }
    }
}

@Composable
private fun CoinRankTopHeader(data: List<CoinRank>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEachIndexed { index, coinRank ->
            CoinRankTopItem(index, coinRank)
        }
    }
}

@Composable
private fun RowScope.CoinRankTopItem(index: Int, coinRank: CoinRank) {
    val iconId = when (index) {
        0 -> R.drawable.vector_rank_no2
        1 -> R.drawable.vector_rank_no1
        else -> R.drawable.vector_rank_no3
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .padding(OffsetLarge)
    ) {
        Image(painter = painterResource(id = iconId), contentDescription = null)
        Text(
            text = coinRank.username,
            maxLines = 1,
            textAlign = TextAlign.Center,
            fontSize = FontSizeMedium,
            color = AppTheme.colors.accent,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = OffsetMedium)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.vector_user_coin),
                contentDescription = null
            )
            Text(
                text = coinRank.coinCount.toString(),
                fontSize = FontSizeSmall,
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CoinRankItem(coinRank: CoinRank) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OffsetLarge)
    ) {
        Text(
            text = coinRank.rank,
            fontSize = FontSizeLarge,
            color = AppTheme.colors.accent,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = coinRank.username,
            fontSize = FontSizeMedium,
            color = AppTheme.colors.accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = OffsetMedium)
        ) {
            Image(
                painter = painterResource(id = R.drawable.vector_user_coin),
                contentDescription = null
            )
            Text(
                text = coinRank.coinCount.toString(),
                fontSize = FontSizeSmall,
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}
