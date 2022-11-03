package com.lee.playcompose.common.ui.widget.header

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.header.ActionMode.*

/**
 * 公共appbarView 单独使用该view在顶部，content为appbar内部更多显示样式
 * @param modifier 属性配置
 * @param title appbarTitle
 * @param navigationIcon 导航icon资源
 * @param navigationClick 导航点击执行函数
 * @param actionIcon 右侧行为按钮icon资源
 * @param actionClick 右侧行为按钮点击执行函数
 * @param backgroundColor appbarView背景颜色
 * @param contentColor appbarView内容颜色
 * @param elevation appbarView底部阴影
 * @param navigationEnable 是否启用导航按钮（显示/隐藏）
 * @param menuVisibilityState action使用menu菜单时，menu菜单显示状态控制
 * @param actionMode action按钮模式 [ActionMode]
 * @param menu 菜单composable组件
 * @param content 内容composable组件
 * @author jv.lee
 * @date 2022/2/21
 */
@Composable
fun AppBarView(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: ImageVector = Icons.Filled.ArrowBack,
    navigationClick: () -> Unit = {},
    @DrawableRes actionIcon: Int = -1,
    actionClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.item,
    contentColor: Color = AppTheme.colors.accent,
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    navigationEnable: Boolean = true,
    menuVisibilityState: MutableState<Boolean> = remember { mutableStateOf(false) },
    actionMode: ActionMode = Default,
    menu: @Composable ColumnScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {}
) {
    Column(modifier = modifier.shadow(elevation)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
        Box {
            TopAppBar(
                {
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
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
                    when (actionMode) {
                        is Button -> {
                            IconButton(onClick = actionClick) {
                                Icon(
                                    painter = painterResource(id = actionIcon),
                                    "",
                                    tint = contentColor
                                )
                            }
                        }
                        is Menu -> {
                            IconButton(onClick = {
                                menuVisibilityState.value = !menuVisibilityState.value
                            }) {
                                Icon(
                                    painter = painterResource(id = actionIcon),
                                    "",
                                    tint = contentColor
                                )
                            }
                            DropdownMenu(
                                expanded = menuVisibilityState.value,
                                onDismissRequest = { menuVisibilityState.value = false },
                                content = menu,
                                modifier = Modifier
                                    .background(
                                        color = AppTheme.colors.item,
                                        shape = RoundedCornerShape(OffsetRadiusMedium)
                                    )
                                    .widthIn(min = 70.dp)
                            )
                        }
                        else -> {
                            Box(modifier = Modifier.size(ToolBarHeight))
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

/**
 * appbarViewContainer模式， 顶部为appbar，[content] 为底部内容区域
 * @param modifier 属性配置
 * @param title appbarTitle
 * @param navigationIcon 导航icon资源
 * @param navigationClick 导航点击执行函数
 * @param actionIcon 右侧行为按钮icon资源
 * @param actionClick 右侧行为按钮点击执行函数
 * @param backgroundColor appbarView背景颜色
 * @param contentColor appbarView内容颜色
 * @param elevation appbarView底部阴影
 * @param navigationEnable 是否启用导航按钮（显示/隐藏）
 * @param menuVisibilityState action使用menu菜单时，menu菜单显示状态控制
 * @param actionMode action按钮模式 [ActionMode]
 * @param menu 菜单composable组件
 * @param content 内容composable组件
 */
@Composable
fun AppBarViewContainer(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: ImageVector = Icons.Filled.ArrowBack,
    navigationClick: () -> Unit = {},
    @DrawableRes actionIcon: Int = -1,
    actionClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.item,
    contentColor: Color = AppTheme.colors.accent,
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    navigationEnable: Boolean = true,
    containerBackground: Color = AppTheme.colors.background,
    appBarContent: @Composable BoxScope.() -> Unit = {},
    menuVisibilityState: MutableState<Boolean> = remember { mutableStateOf(false) },
    actionMode: ActionMode = Default,
    menu: @Composable ColumnScope.() -> Unit = {},
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
            menuVisibilityState,
            actionMode,
            menu,
            appBarContent
        )
    }
}

@Composable
fun TextMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = FontSizeMedium,
        color = AppTheme.colors.accent,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = OffsetMedium, end = OffsetMedium)
    )
}

@Composable
fun MenuSpacer() {
    Spacer(
        modifier = Modifier
            .padding(top = OffsetSmall, bottom = OffsetSmall)
            .height(1.dp)
            .fillMaxWidth()
            .background(color = AppTheme.colors.accent)
    )
}

/**
 * [Button] 单icon点击事件模式
 * [Menu] 更多点击事件模式（点击后弹出菜单）
 * [Default] 默认什么都不显示
 */
sealed class ActionMode {
    object Button : ActionMode()
    object Menu : ActionMode()
    object Default : ActionMode()
}