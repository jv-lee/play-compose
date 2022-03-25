package com.lee.playcompose.route

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.navigation.animation.composable
import com.lee.playcompose.account.ui.page.LoginPage
import com.lee.playcompose.account.ui.page.RegisterPage
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.WeakDataHolder
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.details.DetailsPage
import com.lee.playcompose.home.ui.page.HomePage
import com.lee.playcompose.me.*
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
import com.lee.playcompose.todo.TodoPage

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
        composable(RoutePage.Home.route) {
            HomePage(navController = navController, paddingValues)
        }

        // module:square
        composable(RoutePage.Square.route) {
            SquarePage(navController = navController, paddingValues)
        }
        sideComposable(RoutePage.Square.MyShare.route) {
            MySharePage(navController = navController)
        }
        sideComposable(RoutePage.Square.CreateShare.route) {
            CreateSharePage(navController = navController)
        }

        // module:system
        composable(RoutePage.System.route) {
            SystemPage(navController = navController, paddingValues)
        }
        sideComposable(RoutePage.System.SystemContentTab.route) {
            val parentTab = WeakDataHolder.instance.getData<ParentTab>(RouteParamsKey.tabDataKey)
            SystemContentTabPage(navController = navController, parentTab)
        }

        // module:me
        composable(RoutePage.Me.route) {
            MePage(navController = navController, paddingValues)
        }
        sideComposable(RoutePage.Me.Coin.route) {
            CoinPage(navController = navController)
        }
        sideComposable(RoutePage.Me.CoinRank.route) {
            CoinRankPage(navController = navController)
        }
        sideComposable(RoutePage.Me.Collect.route) {
            CollectPage(navController = navController)
        }
        sideComposable(RoutePage.Me.Settings.route) {
            SettingsPage(navController = navController)
        }

        // module:todoModel
        sideComposable(RoutePage.Todo.route) {
            TodoPage(navController = navController)
        }

        // module:official
        sideComposable(RoutePage.Official.route) {
            OfficialPage(navController = navController)
        }

        // module:project
        sideComposable(RoutePage.Project.route) {
            ProjectPage(navController = navController)
        }

        // module:search
        sideComposable(RoutePage.Search.route) {
            SearchPage(navController = navController)
        }
        sideComposable(
            route = RoutePage.Search.SearchResult.parseRoute(),
            arguments = RoutePage.Search.SearchResult.parseArguments()
        ) { entry ->
            val searchKey = entry.arguments?.getString(RouteParamsKey.searchKey)
            SearchResultPage(navController = navController, searchKey = searchKey ?: "")
        }

        // module:details
        sideComposable(
            route = RoutePage.Details.parseRoute(),
            arguments = RoutePage.Details.parseArguments()
        ) { entry ->
            val detailsJson = entry.arguments?.getString(RouteParamsKey.detailsDataKey)
            val details =
                HttpManager.getGson().fromJson(detailsJson, DetailsData::class.java)
            DetailsPage(navController = navController, details)
        }

        // module:account
        sideComposable(route = RoutePage.Account.Login.route) {
            LoginPage(navController = navController)
        }
        sideComposable(route = RoutePage.Account.Register.route) {
            RegisterPage(navController = navController)
        }
    }
}