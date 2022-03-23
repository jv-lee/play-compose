package com.lee.playcompose.route

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.navigation.animation.composable
import com.lee.playcompose.account.LoginPage
import com.lee.playcompose.account.RegisterPage
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.WeakDataHolder
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.details.DetailsPage
import com.lee.playcompose.home.ui.page.HomePage
import com.lee.playcompose.me.MePage
import com.lee.playcompose.official.ui.page.OfficialPage
import com.lee.playcompose.project.ui.page.ProjectPage
import com.lee.playcompose.router.*
import com.lee.playcompose.search.ui.page.SearchPage
import com.lee.playcompose.search.ui.page.SearchResultPage
import com.lee.playcompose.square.ui.page.CreateSharePage
import com.lee.playcompose.square.ui.page.MySharePage
import com.lee.playcompose.square.ui.page.SquarePage
import com.lee.playcompose.system.ui.page.SystemContentTabPage
import com.lee.playcompose.system.ui.page.SystemPage

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description app路由配置 (页面路由注册)
 */
@OptIn(ExperimentalCoilApi::class)
fun Activity.appRouteManifest(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
    paddingValues: PaddingValues
) {
    navGraphBuilder.apply {
        // module:home
        composable(PageRoute.Home.route) {
            HomePage(navController = navController, paddingValues)
        }

        // module:square
        composable(PageRoute.Square.route) {
            SquarePage(navController = navController, paddingValues)
        }
        sideComposable(PageRoute.MyShare.route) {
            MySharePage(navController = navController)
        }
        sideComposable(PageRoute.CreateShare.route) {
            CreateSharePage(navController = navController)
        }

        // module:system
        composable(PageRoute.System.route) {
            SystemPage(navController = navController, paddingValues)
        }
        sideComposable(PageRoute.SystemContentTab.route) {
            val parentTab = WeakDataHolder.instance.getData<ParentTab>(ParamsKey.tabDataKey)
            SystemContentTabPage(navController = navController, parentTab)
        }

        // module:me
        composable(PageRoute.Me.route) {
            MePage(navController = navController, paddingValues)
        }

        // module:official
        sideComposable(PageRoute.Official.route) {
            OfficialPage(navController = navController)
        }

        // module:project
        sideComposable(PageRoute.Project.route) {
            ProjectPage(navController = navController)
        }

        // module:search
        sideComposable(PageRoute.Search.route) {
            SearchPage(navController = navController)
        }
        sideComposable(
            route = PageRoute.SearchResult.parseRoute(),
            arguments = PageRoute.SearchResult.parseArguments()
        ) { entry ->
            val searchKey = entry.arguments?.getString(ParamsKey.searchKey)
            SearchResultPage(navController = navController, searchKey = searchKey ?: "")
        }

        // module:details
        sideComposable(
            route = PageRoute.Details.parseRoute(),
            arguments = PageRoute.Details.parseArguments()
        ) { entry ->
            val detailsJson = entry.arguments?.getString(ParamsKey.detailsDataKey)
            val details =
                HttpManager.getGson().fromJson(detailsJson, DetailsData::class.java)
            DetailsPage(navController = navController, details)
        }

        // module:account
        sideComposable(route = PageRoute.Login.route) {
            LoginPage(navController = navController)
        }
        sideComposable(route = PageRoute.Register.route) {
            RegisterPage(navController = navController)
        }
    }
}