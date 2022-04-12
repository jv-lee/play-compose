package com.lee.playcompose.common.ui.widget

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeLarge
import com.lee.playcompose.common.ui.theme.FontSizeMedium

/**
 * @author jv.lee
 * @date 2022/4/12
 * @description
 */
annotation class SelectItemStyle {
    companion object {
        const val GONE = 0
        const val LINE = 1
        const val ITEM = 2
    }
}

@SuppressLint("ModifierParameter")
@Composable
fun <T : Any> WheelView(
    state: PagerSnapState = rememberPagerSnapState(),
    data: List<T>,
    findText: ((T) -> String),
    @SelectItemStyle style: Int = SelectItemStyle.GONE,
    itemHeight: Dp = 40.dp,
    itemSelectedBackground: Color = AppTheme.colors.onFocus,
    selectedTextColor: Color = AppTheme.colors.accent,
    unSelectedTextColor: Color = AppTheme.colors.primary,
    selectedTextSize: TextUnit = FontSizeLarge,
    unSelectedTextSize: TextUnit = FontSizeMedium,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(itemHeight * 3)) {
        WheelViewItemBackground(
            style = style,
            itemHeight = itemHeight,
            itemSelectedBackground = itemSelectedBackground
        )

        ComposePagerSnapHelper(state = state, itemOffset = itemHeight) { listState ->
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                item { Spacer(modifier = Modifier.height(itemHeight)) }
                itemsIndexed(data) { index, item ->
                    val isSelected = index == state.currentIndex
                    Text(
                        text = findText(item),
                        fontSize = if (isSelected) selectedTextSize else unSelectedTextSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) selectedTextColor else unSelectedTextColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                item { Spacer(modifier = Modifier.height(itemHeight)) }
            }
        }
    }
}

@Composable
private fun BoxScope.WheelViewItemBackground(
    @SelectItemStyle style: Int,
    itemHeight: Dp,
    itemSelectedBackground: Color
) {
    when (style) {
        SelectItemStyle.LINE -> {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .offset(y = itemHeight)
                    .background(color = itemSelectedBackground)
                    .align(alignment = Alignment.TopCenter)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .offset(y = -itemHeight)
                    .background(color = itemSelectedBackground)
                    .align(alignment = Alignment.BottomCenter)
            )
        }
        SelectItemStyle.ITEM -> {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(color = itemSelectedBackground)
                    .align(alignment = Alignment.Center)
            )
        }
        else -> {
        }
    }
}