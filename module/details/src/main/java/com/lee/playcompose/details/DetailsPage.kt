package com.lee.playcompose.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.extensions.toast

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun DetailsPage(navController: NavController, data: DetailsData) {
    val webState = rememberWebViewState(url = data.link)
    WebView(state = webState, modifier = Modifier.fillMaxSize())
}