package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.BottomSlideAnimatedVisible
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ui.theme.AppTheme

/**
 * 网络错误全局Snackbar 底部提示
 * @author jv.lee
 * @date 2022/7/12
 */
@Composable
fun NetworkErrorAlert(visible: Boolean) {
    BottomSlideAnimatedVisible(visible = visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(align = Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            Snackbar(backgroundColor = AppTheme.colors.item, shape = RoundedCornerShape(0.dp)) {
                Text(
                    text = app.getString(R.string.network_not_access),
                    style = TextStyle(color = AppTheme.colors.accent)
                )
            }
        }
    }
}