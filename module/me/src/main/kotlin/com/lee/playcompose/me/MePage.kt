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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.ProfileItem
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.AppHeaderContainer
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 首页第四个tab 我的页面
 */
@Composable
fun MePage(navController: NavController, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
    ) {
        MeHeader(notLoginClick = {})
        MeLineItemList(onItemClick = { route ->
            toast(route)
        })
    }
}

@Composable
private fun MeHeader(isLogin: Boolean = false, notLoginClick: () -> Unit) {
    AppHeaderContainer(
        headerBrush = false,
        modifier = Modifier
            .background(AppTheme.colors.item)
            .clickable { if (!isLogin) notLoginClick() }
    ) {
        ConstraintLayout(modifier = Modifier.height(160.dp)) {
            val (header, username, level) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.vector_account),
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
                text = "未登陆",
                color = AppTheme.colors.accent,
                fontSize = FontSizeLarge,
                modifier = Modifier
                    .padding(OffsetSmall)
                    .constrainAs(username) {
                        start.linkTo(header.end, 26.dp)
                        top.linkTo(header.top)
                        bottom.linkTo(level.top)
                    })
            if (isLogin) {
                Text(text = "Level",
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
    object Coin : MeItem("我的积分", R.string.me_item_coin, R.drawable.vector_coin)
    object Collect : MeItem("我的收藏", R.string.me_item_collect, R.drawable.vector_collect)
    object Share : MeItem("我的分享", R.string.me_item_share, R.drawable.vector_share)
    object TODO : MeItem("TODO", R.string.me_item_todo, R.drawable.vector_todo)
    object Settings : MeItem("系统设置", R.string.me_item_settings, R.drawable.vector_settings)
}