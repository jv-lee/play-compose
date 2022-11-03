package com.lee.playcompose.common.ui.widget

import androidx.compose.animation.AnimatedVisibility
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
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.lee.playcompose.base.extensions.delayState
import com.lee.playcompose.common.R
import com.lee.playcompose.common.ui.theme.AppTheme

/**
 * 状态控制容器
 * @param status 当前页面状态
 * [UiStatus.Loading] 加载状态
 * [UiStatus.Failed] 失败状态
 * [UiStatus.Complete] 完成状态
 * @param retry 重试执行函数
 * @param content 内容composable元素
 * @author jv.lee
 * @date 2022/3/14
 */
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
fun UiStatusListPage(
    loadState: CombinedLoadStates,
    retry: () -> Unit,
    content: @Composable () -> Unit
) {
    // loadPage Load.
    if (loadState.refresh is LoadState.Loading) {
        PageLoading()
        return
    }

    // loadPage Error.
    if (loadState.refresh is LoadState.Error) {
        PageError { retry() }
        return
    }

    content()
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
    val visible = delayState(default = false, update = true)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        AnimatedVisibility(visible = visible.value) {
            CircularProgressIndicator(
                color = AppTheme.colors.accent,
                modifier = Modifier.height(50.dp)
            )
        }
    }
}

sealed class UiStatus {
    object Loading : UiStatus()
    object Failed : UiStatus()
    object Complete : UiStatus()
}