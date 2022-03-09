package com.lee.playcompose.common.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
    @DrawableRes leftDrawable: Int? = null,
    @DrawableRes rightDrawable: Int? = null,
    leftText: String? = null,
    rightText: String? = null,
    rightSwitchEnable: Boolean = false,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .background(AppTheme.colors.item)
                .fillMaxWidth()
                .padding(16.dp, 12.dp, 16.dp, 12.dp)
                .wrapContentHeight()
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
                        text = it,
                        color = AppTheme.colors.accent,
                        fontSize = FontSizeMedium,
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }
            }

            Row(modifier = Modifier.align(alignment = Alignment.CenterEnd)) {
                if (rightSwitchEnable) {
                    Switch(checked = true, onCheckedChange = {})
                    return@Row
                }
                rightText?.let {
                    Text(
                        text = it,
                        color = AppTheme.colors.primary,
                        fontSize = FontSizeSmall,
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
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
}