package com.lee.playcompose.details

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.just.agentweb.AgentWeb
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.extensions.bindLifecycle
import com.lee.playcompose.common.extensions.setWebBackEvent
import com.lee.playcompose.common.ui.widget.AppBarViewContainer
import com.lee.playcompose.common.R as CR

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description 内容详情页面 （webView）
 */
@Composable
fun Activity.DetailsPage(navController: NavController, details: DetailsData) {
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
            post {
                AgentWeb.with(this@WebView)
                    .setAgentWebParent(this, FrameLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator(CR.color.colorThemeAccent)
                    .createAgentWeb()
                    .ready()
                    .go(details.link)
                    .bindLifecycle(lifecycle)
                    .apply {
                        webCreator.webView.setWebBackEvent()
                    }
            }
        }
    })
}