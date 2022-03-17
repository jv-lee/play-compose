package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsHeight
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeLarge

/**
 * @author jv.lee
 * @date 2022/2/21
 * @description 公共appbarView
 */
@Composable
fun AppBarView(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: ImageVector = Icons.Filled.ArrowBack,
    navigationClick: () -> Unit = {},
    actionIcon: ImageVector = Icons.Filled.MoreVert,
    actionClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.item,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    navigationEnable: Boolean = true,
    actionEnable: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Column(modifier = modifier.shadow(elevation)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
                .background(backgroundColor)
        )
        Box {
            TopAppBar(
                {
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .wrapContentSize(Alignment.Center),
                        fontSize = FontSizeLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                },
                Modifier.fillMaxWidth(),
                navigationIcon = {
                    if (navigationEnable) {
                        IconButton(onClick = navigationClick) {
                            Icon(navigationIcon, "", tint = contentColor)
                        }
                    }
                },
                actions = {
                    if (actionEnable) {
                        IconButton(onClick = actionClick) {
                            Icon(actionIcon, "", tint = contentColor)
                        }
                    }
                },
                backgroundColor,
                contentColor,
                0.dp
            )
            content()
        }
    }
}

@Composable
fun AppBarViewContainer(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: ImageVector = Icons.Filled.ArrowBack,
    navigationClick: () -> Unit = {},
    actionIcon: ImageVector = Icons.Filled.MoreVert,
    actionClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.item,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    navigationEnable: Boolean = true,
    actionEnable: Boolean = false,
    containerBackground: Color = AppTheme.colors.background,
    appBarContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .background(containerBackground)
            .fillMaxSize()
    ) {
        Column {
            HeaderSpacer()
            content()
        }
        AppBarView(
            modifier,
            title,
            navigationIcon,
            navigationClick,
            actionIcon,
            actionClick,
            backgroundColor,
            contentColor,
            elevation,
            navigationEnable,
            actionEnable
        )
    }
}