package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ui.theme.AppTheme

/**
 * @author jv.lee
 * @date 2022/3/14
 * @description
 */
sealed class UiStatus {
    object Loading : UiStatus()
    object Failed : UiStatus()
    object Complete : UiStatus()
}

@Composable
fun UiStatusPage(
    status: UiStatus = UiStatus.Loading,
    retry: () -> Unit,
    content: @Composable () -> Unit
) {
    when (status) {
        is UiStatus.Loading -> PageLoading()
        is UiStatus.Failed -> PageError(retry)
        is UiStatus.Complete -> content()
    }
}


@Composable
private fun PageError(retry: () -> Unit = { }) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        Column(modifier = Modifier.clickable { retry() }) {
            Image(
                painter = painterResource(id = R.drawable.vector_failed),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(id = R.string.page_load_error),
                color = AppTheme.colors.accent,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PageLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        CircularProgressIndicator(color = AppTheme.colors.accent, modifier = Modifier.height(50.dp))
    }
}