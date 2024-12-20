package com.lee.playcompose.square.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.base.ktx.onTap
import com.lee.playcompose.base.ktx.setResult
import com.lee.playcompose.common.ktx.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.ToolBarHeight
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.CreateShareViewEvent
import com.lee.playcompose.square.viewmodel.CreateShareViewIntent
import com.lee.playcompose.square.viewmodel.CreateShareViewModel
import com.lee.playcompose.square.viewmodel.CreateShareViewState

/**
 * 创建分享内容页面
 * @author jv.lee
 * @date 2022/3/16
 */
@Composable
fun CreateSharePage(
    navController: NavController = LocalNavController.current,
    viewModel: CreateShareViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val viewState = viewModel.viewStates()
    val shareSuccess = stringResource(id = R.string.share_success)

    // 监听单发事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is CreateShareViewEvent.CreateSuccess -> {
                    toast(shareSuccess)
                    navController.setResult(REQUEST_KEY_SHARE_REFRESH)
                    navController.popBackStack()
                }

                is CreateShareViewEvent.CreateFailed -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    AppBarViewContainer(
        title = stringResource(id = R.string.square_create_share_title),
        navigationClick = {
            focusManager.clearFocus()
            navController.popBackStack()
        },
        content = {
            CreateShareContent(
                viewState = viewState,
                clearFocus = { focusManager.clearFocus() },
                onChangeTitle = {
                    viewModel.dispatch(CreateShareViewIntent.ChangeShareTitle(it))
                },
                onChangeContent = {
                    viewModel.dispatch(CreateShareViewIntent.ChangeShareContent(it))
                },
                onActionShare = {
                    viewModel.dispatch(CreateShareViewIntent.RequestShare)
                }
            )
        }
    )
}

@Composable
private fun CreateShareContent(
    viewState: CreateShareViewState,
    clearFocus: () -> Unit,
    onChangeTitle: (String) -> Unit,
    onChangeContent: (String) -> Unit,
    onActionShare: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .onTap { clearFocus() }
            .fillMaxSize()
    ) {
        val (tvTitle, editTitle, tvContent, editContent, tvDescription) = createRefs()
        Text(
            text = stringResource(id = R.string.share_title_text),
            fontSize = FontSizeTheme.sizes.large,
            color = ColorsTheme.colors.accent,
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(parent.top, OffsetMedium)
                start.linkTo(parent.start, OffsetLarge)
            }
        )

        AppTextField(
            value = viewState.shareTitle,
            onValueChange = onChangeTitle,
            hintText = stringResource(id = R.string.share_title_hint),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .height(ToolBarHeight)
                .fillMaxWidth()
                .constrainAs(editTitle) {
                    top.linkTo(tvTitle.bottom)
                }
        )

        Text(
            text = stringResource(id = R.string.share_content_text),
            fontSize = FontSizeTheme.sizes.large,
            color = ColorsTheme.colors.accent,
            modifier = Modifier.constrainAs(tvContent) {
                top.linkTo(editTitle.bottom)
                start.linkTo(parent.start, OffsetLarge)
            }
        )

        AppTextField(
            value = viewState.shareContent,
            onValueChange = onChangeContent,
            hintText = stringResource(id = R.string.share_content_hint),
            keyboardActions = KeyboardActions(onSend = { onActionShare() }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            modifier = Modifier
                .height(ToolBarHeight)
                .fillMaxWidth()
                .constrainAs(editContent) {
                    top.linkTo(tvContent.bottom)
                }
        )

        Text(
            text = stringResource(id = R.string.share_description),
            fontSize = FontSizeTheme.sizes.small,
            color = ColorsTheme.colors.primary,
            letterSpacing = 4.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = OffsetLarge, end = OffsetLarge)
                .constrainAs(tvDescription) {
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}
