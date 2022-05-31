package com.lee.playcompose.common.ui.widget

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
import com.google.accompanist.insets.statusBarsHeight
import com.lee.playcompose.common.ui.composable.HeaderSpacer
import com.lee.playcompose.common.ui.theme.*

/**
 * 公共appbarView
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
    actionMode: ActionMode = ActionMode.Default,
    menu: @Composable ColumnScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
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
                        is ActionMode.Button -> {
                            IconButton(onClick = actionClick) {
                                Icon(
                                    painter = painterResource(id = actionIcon),
                                    "",
                                    tint = contentColor
                                )
                            }
                        }
                        is ActionMode.Menu -> {
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
                                modifier = Modifier.background(
                                    color = AppTheme.colors.item, shape = RoundedCornerShape(
                                        OffsetRadiusSmall
                                    )
                                )
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
    actionMode: ActionMode = ActionMode.Default,
    menu: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit,
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
            appBarContent,
        )
    }
}

@Composable
fun TextMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = AppTheme.colors.accent,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(
                start = OffsetLarge,
                end = OffsetLarge,
                top = OffsetSmall,
                bottom = OffsetSmall
            )
    )
}

@Composable
fun MenuSpacer() {
    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = AppTheme.colors.accent)
    )
}

sealed class ActionMode {
    object Button : ActionMode()
    object Menu : ActionMode()
    object Default : ActionMode()
}