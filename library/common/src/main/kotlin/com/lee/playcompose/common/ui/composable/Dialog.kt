package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ui.theme.*

/**
 * @author jv.lee
 * @date 2022/4/2
 * @description
 */
@Composable
fun LoadingDialog(isShow: Boolean) {
    if (isShow) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .size(width = 100.dp, height = 78.dp)
                    .background(
                        color = AppTheme.colors.item,
                        shape = RoundedCornerShape(OffsetRadiusMedium)
                    )
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    isShow: Boolean,
    titleText: String = "",
    contentText: String? = null,
    cancelText: String = stringResource(id = R.string.dialog_cancel),
    confirmText: String = stringResource(id = R.string.dialog_confirm),
    singleConfirm: Boolean = false,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    if (isShow) {
        Dialog(onDismissRequest = { }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .width(width = 240.dp)
                    .background(
                        color = AppTheme.colors.item,
                        shape = RoundedCornerShape(OffsetRadiusMedium)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = OffsetLarge, bottom = OffsetLarge)
                        .wrapContentSize(Alignment.Center),
                ) {
                    Text(
                        text = titleText,
                        fontSize = FontSizeMedium,
                        color = AppTheme.colors.accent,
                        modifier = Modifier.padding(top = OffsetMedium, bottom = OffsetMedium)
                    )
                    contentText?.let {
                        Text(
                            text = it,
                            fontSize = FontSizeSmall,
                            color = AppTheme.colors.primary,
                            modifier = Modifier.padding(top = OffsetMedium, bottom = OffsetMedium)
                        )
                    }
                }
                HorizontallySpacer()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    if (!singleConfirm) {
                        Text(
                            text = cancelText,
                            fontSize = FontSizeMedium,
                            color = AppTheme.colors.accent,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(bottomStart = OffsetRadiusMedium))
                                .clickable { onCancel() }
                                .wrapContentSize(Alignment.Center)
                        )
                        VerticallySpacer()
                    }
                    Text(
                        text = confirmText,
                        fontSize = FontSizeMedium,
                        color = AppTheme.colors.accent,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(bottomEnd = OffsetRadiusMedium))
                            .clickable { onConfirm() }
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    }
}