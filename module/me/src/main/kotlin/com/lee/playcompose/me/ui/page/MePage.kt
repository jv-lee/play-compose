package com.lee.playcompose.me.ui.page

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.common.entity.AccountViewState
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.ProfileItem
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.AppHeaderContainer
import com.lee.playcompose.common.ui.widget.RouteBackHandler
import com.lee.playcompose.me.R
import com.lee.playcompose.me.model.entity.MeItem
import com.lee.playcompose.me.viewmodel.MeViewModel
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.common.R as CR

/**
 * 首页第四个tab 我的页面
 * @author jv.lee
 * @date 2022/2/24
 */
@Composable
fun MePage(
    navController: NavController = LocalNavController.current,
    viewModel: MeViewModel = viewModel()
) {
    val accountViewState = viewModel.accountService.getAccountViewStates(LocalActivity.current)

    // double click close app.
    RouteBackHandler()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MeHeader(accountViewState = accountViewState, notLoginClick = {
            navController.navigateArgs(RoutePage.Account.Login.route)
        })
        MeLineItemList(viewModel.viewStates.meItems, onItemClick = { route ->
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
private fun MeLineItemList(meItems: List<MeItem>, onItemClick: (String) -> Unit) {
    Column {
        meItems.forEach {
            MeLineItem(it, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun MeLineItem(meItem: MeItem, onItemClick: (String) -> Unit) {
    ProfileItem(
        leftDrawable = meItem.icon,
        leftText = stringResource(id = meItem.name),
        rightDrawable = meItem.arrow,
        modifier = Modifier.padding(top = 1.dp)
    ) {
        onItemClick(meItem.route)
    }
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
        navController.navigateArgs(route)
        return
    }

    // 需要校验登陆状态
    if (accountViewState.isLogin) {
        navController.navigateArgs(route)
    } else {
        toast(app.getString(R.string.me_login_message))
        navController.navigateArgs(RoutePage.Account.Login.route)
    }
}