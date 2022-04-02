package com.lee.playcompose.me.ui.page

import androidx.compose.foundation.background
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
import com.lee.playcompose.common.extensions.toast
import com.lee.playcompose.common.ui.composable.LoadingDialog
import com.lee.playcompose.common.ui.composable.ProfileItem
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.me.R
import com.lee.playcompose.me.viewmodel.SettingsViewAction
import com.lee.playcompose.me.viewmodel.SettingsViewEvent
import com.lee.playcompose.me.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/3/25
 * @description 设置页面
 */
@Composable
fun SettingsPage(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val activity = LocalActivity.current
    val coroutine = rememberCoroutineScope()
    val accountState = viewModel.accountService.getAccountViewStates(activity = activity)
    val viewState = viewModel.viewStates

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                is SettingsViewEvent.ClearCacheResult -> {
                    toast(event.message)
                }
            }
        }
    }

    LoadingDialog(isShow = viewState.isLoading || accountState.isLoading)
    AppBarViewContainer(
        title = stringResource(id = R.string.me_item_settings),
        navigationClick = {
            navController.popBackStack()
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
        ) {
            ProfileItem(
                leftText = stringResource(id = R.string.dark_mode_system),
                rightSwitchVisible = true,
                modifier = Modifier.padding(top = OffsetMedium)
            )
            ProfileItem(
                leftText = stringResource(id = R.string.dark_mode_night),
                rightSwitchVisible = true,
                modifier = Modifier.padding(top = 1.dp)
            )
            ProfileItem(
                leftText = stringResource(id = R.string.settings_clear_text),
                rightText = viewState.totalCacheSize,
                rightDrawable = CR.drawable.vector_arrow,
                modifier = Modifier.padding(top = OffsetMedium)
            ) {
                viewModel.dispatch(SettingsViewAction.RequestClearCache)
            }
            if (accountState.isLogin) {
                ProfileItem(
                    leftText = stringResource(id = R.string.settings_logout),
                    leftDrawable = R.drawable.vector_logout,
                    rightDrawable = CR.drawable.vector_arrow,
                    modifier = Modifier.padding(top = OffsetMedium)
                ) {
                    coroutine.launch {
                        viewModel.accountService.requestLogout(activity = activity)
                    }
                }
            }

        }
    }
}
