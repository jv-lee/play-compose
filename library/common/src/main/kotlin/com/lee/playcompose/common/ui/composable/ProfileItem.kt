package com.lee.playcompose.common.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme

/**
 * 项目通用设置项itemView
 * @param modifier 样式参数
 * @param leftDrawable 左侧icon
 * @param rightDrawable 右侧icon
 * @param leftText 左侧文案
 * @param rightText 右侧文案
 * @param rightSwitchEnable 右侧switch是否可点击
 * @param rightSwitchVisible 右侧switch是否显示
 * @param switchChecked switch是否选中
 * @param onCheckedChange 选中switchChange回调
 * @param onClick 点击item执行函数
 * @author jv.lee
 * @date 2022/3/9
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
            .background(ColorsTheme.colors.item)
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
                    color = ColorsTheme.colors.accent,
                    fontSize = FontSizeTheme.sizes.medium,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
        }

        Row(modifier = Modifier.align(alignment = Alignment.CenterEnd)) {
            rightText?.let {
                Text(
                    text = rightText,
                    color = ColorsTheme.colors.primary,
                    fontSize = FontSizeTheme.sizes.small,
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
                        checkedThumbColor = ColorsTheme.colors.focus,
                        checkedTrackColor = ColorsTheme.colors.onFocus
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