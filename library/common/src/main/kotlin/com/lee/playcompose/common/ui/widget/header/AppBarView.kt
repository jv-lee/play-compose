package com.lee.playcompose.common.ui.widget.header

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
import com.lee.playcompose.common.ui.theme.ShadowSmall
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.header.ActionMode.Button
import com.lee.playcompose.common.ui.widget.header.ActionMode.Default
import com.lee.playcompose.common.ui.widget.header.ActionMode.Menu

/**
 * 公共appbarView 单独使用该view在顶部，content为appbar内部更多显示样式
 * @param title appbarTitle
 * @param navigationIcon 导航icon资源
 * @param navigationClick 导航点击执行函数
 * @param actionIcon 右侧行为按钮icon资源
 * @param actionClick 右侧行为按钮点击执行函数
 * @param backgroundColor appbarView背景颜色
 * @param contentColor appbarView内容颜色
 * @param navigationEnable 是否启用导航按钮（显示/隐藏）
 * @param menuVisibilityState action使用menu菜单时，menu菜单显示状态控制
 * @param actionMode action按钮模式 [ActionMode]
 * @param menu 菜单composable组件
 * @param content 内容composable组件
 * @author jv.lee
 * @date 2022/2/21
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarView(
    title: String = "",
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationClick: () -> Unit = {},
    @DrawableRes actionIcon: Int = -1,
    actionClick: () -> Unit = {},
    backgroundColor: Color = ColorsTheme.colors.item,
    contentColor: Color = ColorsTheme.colors.accent,
    navigationEnable: Boolean = true,
    menuVisibilityState: MutableState<Boolean> = remember { mutableStateOf(false) },
    actionMode: ActionMode = Default,
    menu: @Composable ColumnScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontSize = FontSizeTheme.sizes.large,
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
                            containerColor = ColorsTheme.colors.item,
                            expanded = menuVisibilityState.value,
                            onDismissRequest = { menuVisibilityState.value = false },
                            content = menu,
                            modifier = Modifier.widthIn(min = 70.dp)
                        )
                    }

                    else -> {
                        Box(modifier = Modifier.size(ToolBarHeight))
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
            expandedHeight = ToolBarHeight,
        )
        Box(
            modifier = Modifier.statusBarsPadding()
        ) {
            content()
        }
    }
}

/**
 * appbarViewContainer模式， 顶部为appbar，[content] 为底部内容区域
 * @param title appbarTitle
 * @param navigationIcon 导航icon资源
 * @param navigationClick 导航点击执行函数
 * @param actionIcon 右侧行为按钮icon资源
 * @param actionClick 右侧行为按钮点击执行函数
 * @param backgroundColor appbarView背景颜色
 * @param contentColor appbarView内容颜色
 * @param navigationEnable 是否启用导航按钮（显示/隐藏）
 * @param menuVisibilityState action使用menu菜单时，menu菜单显示状态控制
 * @param actionMode action按钮模式 [ActionMode]
 * @param appBarContent appBar内部组件
 * @param appBarFooter appBar底部组件（tab）
 * @param menu 菜单composable组件
 * @param content 内容composable组件
 */
@Composable
fun AppBarViewContainer(
    title: String = "",
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationClick: () -> Unit = {},
    @DrawableRes actionIcon: Int = -1,
    actionClick: () -> Unit = {},
    backgroundColor: Color = ColorsTheme.colors.item,
    contentColor: Color = ColorsTheme.colors.accent,
    navigationEnable: Boolean = true,
    containerBackground: Color = ColorsTheme.colors.background,
    menuVisibilityState: MutableState<Boolean> = remember { mutableStateOf(false) },
    actionMode: ActionMode = Default,
    appBarContent: @Composable BoxScope.() -> Unit = {},
    appBarFooter: @Composable ColumnScope.() -> Unit = {},
    menu: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        Modifier
            .background(containerBackground)
            .fillMaxWidth()
    ) {
        Surface(
            tonalElevation = ShadowSmall,
            shadowElevation = ShadowSmall,
            color = Color.Transparent,
            contentColor = Color.Transparent,
            modifier = Modifier.zIndex(2f)
        ) {
            Column {
                AppBarView(
                    title,
                    navigationIcon,
                    navigationClick,
                    actionIcon,
                    actionClick,
                    backgroundColor,
                    contentColor,
                    navigationEnable,
                    menuVisibilityState,
                    actionMode,
                    menu,
                    appBarContent
                )
                appBarFooter()
            }
        }

        Box(modifier = Modifier.zIndex(1f)) {
            content()
        }
    }

}

@Composable
fun TextMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = FontSizeTheme.sizes.medium,
        color = ColorsTheme.colors.accent,
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
            .background(color = ColorsTheme.colors.accent)
    )
}

/**
 * [Button] 单icon点击事件模式
 * [Menu] 更多点击事件模式（点击后弹出菜单）
 * [Default] 默认什么都不显示
 */
sealed class ActionMode {
    data object Button : ActionMode()
    data object Menu : ActionMode()
    data object Default : ActionMode()
}