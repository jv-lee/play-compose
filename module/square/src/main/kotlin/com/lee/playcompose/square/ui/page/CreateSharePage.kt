package com.lee.playcompose.square.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.bus.ChannelBus
import com.lee.playcompose.base.extensions.onTap
import com.lee.playcompose.common.entity.CreateShareSuccessEvent
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.square.R
import com.lee.playcompose.square.viewmodel.CreateShareViewAction
import com.lee.playcompose.square.viewmodel.CreateShareViewEvent
import com.lee.playcompose.square.viewmodel.CreateShareViewModel
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description 创建分享内容页面
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateSharePage(navController: NavController, viewModel: CreateShareViewModel = viewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = viewModel.viewStates
    val shareSuccess = stringResource(id = R.string.share_success)

    // 监听单发事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is CreateShareViewEvent.CreateSuccess -> {
                    ChannelBus.getChannel<CreateShareSuccessEvent>()
                        ?.send(CreateShareSuccessEvent())
                    toast(shareSuccess)
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
            keyboardController?.hide()
            navController.popBackStack()
        }) {
        ConstraintLayout(
            modifier = Modifier
                .onTap { keyboardController?.hide() }
                .fillMaxSize()
                .background(AppTheme.colors.background)
        ) {
            val (tvTitle, editTitle, tvContent, editContent, tvDescription) = createRefs()
            Text(
                text = stringResource(id = R.string.share_title_text),
                fontSize = FontSizeLarge,
                color = AppTheme.colors.accent,
                modifier = Modifier.constrainAs(tvTitle) {
                    top.linkTo(parent.top, OffsetMedium)
                    start.linkTo(parent.start, OffsetLarge)
                }
            )

            AppTextField(
                value = viewState.shareTitle,
                onValueChange = { viewModel.dispatch(CreateShareViewAction.ChangeShareTitle(it)) },
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
                    })

            Text(
                text = stringResource(id = R.string.share_content_text),
                fontSize = FontSizeLarge,
                color = AppTheme.colors.accent,
                modifier = Modifier.constrainAs(tvContent) {
                    top.linkTo(editTitle.bottom)
                    start.linkTo(parent.start, OffsetLarge)
                }
            )

            AppTextField(
                value = viewState.shareContent,
                onValueChange = { viewModel.dispatch(CreateShareViewAction.ChangeShareContent(it)) },
                hintText = stringResource(id = R.string.share_content_hint),
                keyboardActions = KeyboardActions(onSend = {
                    viewModel.dispatch(CreateShareViewAction.RequestShare)
                }),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                modifier = Modifier
                    .height(ToolBarHeight)
                    .fillMaxWidth()
                    .constrainAs(editContent) {
                        top.linkTo(tvContent.bottom)
                    })

            Text(
                text = stringResource(id = R.string.share_description),
                fontSize = FontSizeSmall,
                color = AppTheme.colors.primary,
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
}
