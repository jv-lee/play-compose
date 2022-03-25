package com.lee.playcompose.me

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.ProfileItem
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.AppHeaderContainer
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 首页第四个tab 我的页面
 */
@Composable
fun MePage(navController: NavController, paddingValues: PaddingValues) {
    val accountViewState =
        ModuleService.find<AccountService>().getAccountViewStates(LocalActivity.current)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        MeHeader(accountViewState = accountViewState, notLoginClick = {
            navController.navigate(RoutePage.Account.Login.route)
        })
        MeLineItemList(onItemClick = { route ->
            itemNavigation(navController, accountViewState, route)
        })
    }
}

@Composable
private fun MeHeader(accountViewState: AccountViewState, notLoginClick: () -> Unit) {
    AppHeaderContainer(
        headerBrush = false,
        modifier = Modifier
            .background(AppTheme.colors.item)
            .clickable { if (!accountViewState.isLogin) notLoginClick() }
    ) {
        ConstraintLayout(modifier = Modifier.height(160.dp)) {
            val (header, username, level) = createRefs()
            Image(
                painter = painterResource(
                    id = if (accountViewState.isLogin)
                        CR.mipmap.ic_launcher_round else R.drawable.vector_account
                ),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(56.dp)
                    .border(BorderWidth, AppTheme.colors.focus, CircleShape)
                    .constrainAs(header) {
                        start.linkTo(parent.start, 36.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
            Text(
                text = if (accountViewState.isLogin)
                    accountViewState.accountData?.userInfo?.nickname ?: "" else
                    stringResource(id = R.string.me_account_default_text),
                color = AppTheme.colors.accent,
                fontSize = FontSizeLarge,
                modifier = Modifier
                    .padding(OffsetSmall)
                    .constrainAs(username) {
                        start.linkTo(header.end, 26.dp)
                        top.linkTo(header.top)
                        bottom.linkTo(level.top)
                    })
            if (accountViewState.isLogin) {
                Text(text = stringResource(
                    id = R.string.me_account_info_text,
                    accountViewState.accountData?.coinInfo?.level ?: "",
                    accountViewState.accountData?.coinInfo?.rank ?: ""
                ),
                    color = AppTheme.colors.focus,
                    fontSize = FontSizeSmall,
                    modifier = Modifier
                        .padding(OffsetSmall)
                        .constrainAs(level) {
                            start.linkTo(header.end, 26.dp)
                            top.linkTo(username.bottom)
                            bottom.linkTo(header.bottom)
                        })
            }
            createVerticalChain(username, level, chainStyle = ChainStyle.Packed)
        }
    }
}

@Composable
private fun MeLineItemList(onItemClick: (String) -> Unit) {
    Column {
        meItems.forEach {
            MeLineItem(it, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun MeLineItem(meItem: MeItem, onItemClick: (String) -> Unit) {
    Spacer(modifier = Modifier.height(1.dp))
    ProfileItem(
        leftDrawable = meItem.icon,
        leftText = meItem.name,
        rightDrawable = meItem.arrow,
    ) {
        onItemClick(meItem.route)
    }
}

private val meItems =
    listOf(MeItem.Coin, MeItem.Collect, MeItem.Share, MeItem.TODO, MeItem.Settings)

private sealed class MeItem(
    val route: String,
    val name: Int,
    val icon: Int,
    val arrow: Int = CR.drawable.vector_arrow
) {
    object Coin : MeItem(RoutePage.Me.Coin.route, R.string.me_item_coin, R.drawable.vector_coin)
    object Collect :
        MeItem(RoutePage.Me.Collect.route, R.string.me_item_collect, R.drawable.vector_collect)

    object Share :
        MeItem(RoutePage.Square.MyShare.route, R.string.me_item_share, R.drawable.vector_share)

    object TODO : MeItem(RoutePage.Todo.route, R.string.me_item_todo, R.drawable.vector_todo)
    object Settings :
        MeItem(RoutePage.Me.Settings.route, R.string.me_item_settings, R.drawable.vector_settings)
}

/**
 * 我的页面item导航
 */
private fun itemNavigation(
    navController: NavController,
    accountViewState: AccountViewState,
    route: String
) {
    // 无需校验登陆状态
    if (route == MeItem.Settings.route) {
        navController.navigate(route)
        return
    }

    // 需要校验登陆状态
    if (accountViewState.isLogin) {
        navController.navigate(route)
    } else {
        toast(app.getString(R.string.me_login_message))
        navController.navigate(RoutePage.Account.Login.route)
    }
}