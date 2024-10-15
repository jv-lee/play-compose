package com.lee.playcompose.details.ui.page

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.just.agentweb.AgentWeb
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.base.tools.DarkModeTools
import com.lee.playcompose.base.utils.ShareUtil
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.extensions.bindLifecycle
import com.lee.playcompose.common.extensions.setWebBackEvent
import com.lee.playcompose.common.extensions.supportDarkMode
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.ui.widget.header.MenuSpacer
import com.lee.playcompose.common.ui.widget.header.TextMenuItem
import com.lee.playcompose.details.R
import com.lee.playcompose.details.viewmodel.DetailsViewIntent
import com.lee.playcompose.details.viewmodel.DetailsViewEvent
import com.lee.playcompose.details.viewmodel.DetailsViewModel
import com.lee.playcompose.base.R as BR
import com.lee.playcompose.common.R as CR

/**
 * 内容详情页面 （webView）
 * @author jv.lee
 * @date 2022/2/24
 */
@Composable
fun DetailsPage(
    details: DetailsData,
    navController: NavController = LocalNavController.current,
    viewModel: DetailsViewModel = viewModel(
        factory = DetailsViewModel.CreateFactory(details)
    )
) {
    val viewState = viewModel.viewStates()
    val menuVisibilityState = remember { mutableStateOf(false) }
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is DetailsViewEvent.CollectEvent -> {
                    toast(event.message)
                }
                is DetailsViewEvent.ShareEvent -> {
                    ShareUtil.shareText(activity, event.shareText)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading)

    AppBarViewContainer(
        title = details.title,
        actionMode = viewState.actionModel,
        actionIcon = BR.drawable.vector_more,
        navigationClick = { navController.popBackStack() },
        menuVisibilityState = menuVisibilityState,
        menu = {
            TextMenuItem(stringResource(id = R.string.menu_collect)) {
                menuVisibilityState.value = false
                viewModel.dispatch(DetailsViewIntent.RequestCollectDetails)
            }
            MenuSpacer()
            TextMenuItem(stringResource(id = R.string.menu_shared)) {
                menuVisibilityState.value = false
                viewModel.dispatch(DetailsViewIntent.ShareDetails)
            }
        }
    ) {
        WebView(details = details)
    }
}

@Composable
private fun WebView(details: DetailsData) {
    val activity = LocalActivity.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val progressColor = ContextCompat.getColor(
        activity,
        if (DarkModeTools.get().isDark) CR.color.colorThemePrimary else CR.color.colorThemeAccent
    )
    AndroidView(factory = { context: Context ->
        FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            post {
                AgentWeb.with(activity)
                    .setAgentWebParent(this, FrameLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator(progressColor)
                    .createAgentWeb()
                    .ready()
                    .go(details.link)
                    .bindLifecycle(lifecycle)
                    .apply {
                        webCreator.webView.setWebBackEvent()
                        DarkModeTools.get().changeDarkModeState()
                        supportDarkMode()
                    }
            }
        }
    })
}