package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.LocalNavController
import com.lee.playcompose.base.extensions.activityViewModel
import com.lee.playcompose.common.entity.AccountViewEvent
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.ConfirmDialog
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.composable.ProfileItem
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.common.viewmodel.ThemeViewAction
import com.lee.playcompose.common.viewmodel.ThemeViewModel
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.SettingsViewIntent
import com.lee.playcompose.me.viewmodel.SettingsViewEvent
import com.lee.playcompose.me.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import com.lee.playcompose.common.R as CR

/**
 * 设置页面
 * @author jv.lee
 * @date 2022/3/25
 */
@Composable
fun SettingsPage(
    navController: NavController = LocalNavController.current,
    viewModel: SettingsViewModel = viewModel(),
    themeViewModel: ThemeViewModel = activityViewModel()
) {
    val activity = LocalActivity.current
    val coroutine = rememberCoroutineScope()
    val viewState = viewModel.viewStates
    val accountState = viewModel.accountService.getAccountViewStates(activity = activity)
    val accountEvent = viewModel.accountService.getAccountViewEvents(activity = activity)
    val themeViewState = themeViewModel.viewStates

    // 监听清除缓存成功事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is SettingsViewEvent.ClearCacheResult -> {
                    toast(event.message)
                }
            }
        }
    }

    // 监听退出登陆成功事件
    LaunchedEffect(Unit) {
        accountEvent.collect { event ->
            when (event) {
                is AccountViewEvent.LogoutSuccess -> {
                    toast(event.message)
                }
                is AccountViewEvent.LogoutFailed -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading || accountState.isLoading)

    ConfirmDialog(
        isShow = viewState.isCacheConfirm,
        titleText = stringResource(id = R.string.settings_clear_title),
        onCancel = {
            viewModel.dispatch(SettingsViewIntent.VisibleCacheDialog(visibility = false))
        },
        onConfirm = { viewModel.dispatch(SettingsViewIntent.RequestClearCache) }
    )

    ConfirmDialog(
        isShow = viewState.isLogoutConfirm,
        titleText = stringResource(id = R.string.settings_logout_title),
        onCancel = {
            viewModel.dispatch(SettingsViewIntent.VisibleLogoutDialog(visibility = false))
        },
        onConfirm = {
            coroutine.launch {
                viewModel.dispatch(SettingsViewIntent.VisibleLogoutDialog(visibility = false))
                viewModel.accountService.requestLogout(activity = activity)
            }
        }
    )

    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_settings),
        navigationClick = {
            navController.popBackStack()
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileItem(
                leftText = stringResource(id = R.string.dark_mode_system),
                rightSwitchEnable = true,
                rightSwitchVisible = true,
                modifier = Modifier.padding(top = OffsetMedium),
                switchChecked = themeViewState.isSystem,
                onCheckedChange = {
                    themeViewModel.dispatch(ThemeViewAction.UpdateSystemAction(it))
                }
            )
            ProfileItem(
                leftText = stringResource(id = R.string.dark_mode_night),
                rightSwitchEnable = !themeViewState.isSystem,
                rightSwitchVisible = true,
                modifier = Modifier.padding(top = 1.dp),
                switchChecked = themeViewState.isDark,
                onCheckedChange = {
                    themeViewModel.dispatch(ThemeViewAction.UpdateDarkAction(it))
                }
            )
            ProfileItem(
                leftText = stringResource(id = R.string.settings_clear_text),
                rightText = viewState.totalCacheSize,
                rightDrawable = CR.drawable.vector_arrow,
                modifier = Modifier.padding(top = OffsetMedium)
            ) {
                viewModel.dispatch(SettingsViewIntent.VisibleCacheDialog(visibility = true))
            }
            if (accountState.isLogin) {
                ProfileItem(
                    leftText = stringResource(id = R.string.settings_logout),
                    leftDrawable = R.drawable.vector_logout,
                    rightDrawable = CR.drawable.vector_arrow,
                    modifier = Modifier.padding(top = OffsetMedium)
                ) {
                    viewModel.dispatch(SettingsViewIntent.VisibleLogoutDialog(visibility = true))
                }
            }
        }
    }
}
