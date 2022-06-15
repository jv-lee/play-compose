package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lee.playcompose.base.extensions.onTap
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.widget.SelectItemStyle
import com.lee.playcompose.common.ui.widget.WheelView
import com.lee.playcompose.todo.viewmodel.SelectTodoTypeViewModel

/**
 * Todo列表类型选择器弹窗
 * @author jv.lee
 * @date 2022/4/12
 */
@Composable
fun SelectTodoTypeDialog(
    isShow: Boolean = false,
    onDismissRequest: (Int) -> Unit,
    viewModel: SelectTodoTypeViewModel = viewModel()
) {
    val viewState = viewModel.viewStates

    if (isShow) {
        Dialog(onDismissRequest = { onDismissRequest(viewState.state.currentIndex) }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onTap { onDismissRequest(viewState.state.currentIndex) }
                    .wrapContentSize(Alignment.Center)
                    .size(width = 220.dp, height = 120.dp)
                    .background(
                        color = AppTheme.colors.item,
                        shape = RoundedCornerShape(OffsetRadiusMedium)
                    )
                    .onTap { }
                    .wrapContentSize(Alignment.Center)
            ) {
                WheelView(
                    state = viewState.state,
                    data = viewState.todoTypes,
                    findText = { it.name },
                    style = SelectItemStyle.LINE,
                )
            }
        }
    }
}