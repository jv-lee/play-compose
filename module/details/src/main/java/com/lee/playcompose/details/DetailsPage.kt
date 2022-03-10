package com.lee.playcompose.details

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.just.agentweb.AgentWeb
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.extensions.bindLifecycle
import com.lee.playcompose.common.extensions.setWebBackEvent
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.router.ParamsKey

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 内容详情页面 （webView）
 */
@Composable
fun Activity.DetailsPage(navController: NavController, bundle: Bundle? = null) {
    val detailsJson = bundle?.getString(ParamsKey.detailsDataKey)
    val details = HttpManager.getGson().fromJson(detailsJson, DetailsData::class.java)

    AppBarViewContainer(title = details.title, navigationClick = { navController.popBackStack() }) {
        WebView(details = details)
    }
}

@Composable
private fun Activity.WebView(details: DetailsData) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    AndroidView(factory = { context: Context ->
        FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }, update = {
        AgentWeb.with(this)
            .setAgentWebParent(it, FrameLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(details.link)
            .bindLifecycle(lifecycle)
            .apply {
                webCreator.webView.setWebBackEvent()
            }
    })
}