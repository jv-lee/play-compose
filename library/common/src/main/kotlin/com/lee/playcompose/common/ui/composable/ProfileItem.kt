package com.lee.playcompose.common.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeMedium
import com.lee.playcompose.common.ui.theme.FontSizeSmall

/**
 * @author jv.lee
 * @date 2022/3/9
 * @description 设置项itemView
 */
@Composable
fun ProfileItem(
    modifier: Modifier = Modifier,
    @DrawableRes leftDrawable: Int? = null,
    @DrawableRes rightDrawable: Int? = null,
    leftText: String? = null,
    rightText: String? = null,
    rightSwitchVisible: Boolean = false,
    rightSwitchEnable: Boolean = false,
    switchChecked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit) = {},
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() }
            .background(AppTheme.colors.item)
            .padding(16.dp, 12.dp, 16.dp, 12.dp)
    ) {
        Row(modifier = Modifier.align(alignment = Alignment.CenterStart)) {
            leftDrawable?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                )
            }

            leftText?.let {
                Text(
                    text = leftText,
                    color = AppTheme.colors.accent,
                    fontSize = FontSizeMedium,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
        }

        Row(modifier = Modifier.align(alignment = Alignment.CenterEnd)) {
            rightText?.let {
                Text(
                    text = rightText,
                    color = AppTheme.colors.primary,
                    fontSize = FontSizeSmall,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
            if (rightSwitchVisible) {
                Switch(
                    enabled = rightSwitchEnable,
                    checked = switchChecked,
                    onCheckedChange = {
                        onCheckedChange(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppTheme.colors.focus,
                        checkedTrackColor = AppTheme.colors.onFocus
                    )
                )
            }
            rightDrawable?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                )
            }
        }
    }
}