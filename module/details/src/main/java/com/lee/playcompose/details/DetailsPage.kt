package com.lee.playcompose.details

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.router.ParamsKey

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */
@Composable
fun DetailsPage(navController: NavController, bundle: Bundle? = null) {
    val detailsJson = bundle?.getString(ParamsKey.detailsDataKey)
    val details = HttpManager.getGson().fromJson(detailsJson, DetailsData::class.java)

    val webState = rememberWebViewState(url = details.link)
    WebView(state = webState, modifier = Modifier.fillMaxSize())
}