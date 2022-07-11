/*
 * app路由配置 (页面路由注册)
 * @author jv.lee
 * @date 2022/3/16
 */
package com.lee.playcompose.route

import androidx.navigation.NavGraphBuilder
import com.lee.playcompose.account.ui.page.LoginPage
import com.lee.playcompose.account.ui.page.RegisterPage
import com.lee.playcompose.base.net.HttpManager
import com.lee.playcompose.base.tools.WeakDataHolder
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.entity.TodoData
import com.lee.playcompose.details.ui.page.DetailsPage
import com.lee.playcompose.home.ui.page.HomePage
import com.lee.playcompose.me.ui.page.*
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
import com.lee.playcompose.todo.ui.page.CreateTodoPage
import com.lee.playcompose.todo.ui.page.TodoPage

// 主页tab默认动画路由集合处理路由动画特殊处理
private val tabDefaultRoutes = arrayListOf(
    RoutePage.Home.route,
    RoutePage.Square.route,
    RoutePage.System.route,
    RoutePage.Me.route
)

// 主页tab缩放动画路由集合处理路由动画特殊处理
private val tabZoomRoutes = arrayListOf(
    RoutePage.Search.route,
    RoutePage.Official.route,
    RoutePage.Project.route,
    RoutePage.Square.CreateShare.route,
    RoutePage.System.SystemContentTab.route,
    RoutePage.Todo.route
)

fun NavGraphBuilder.appRouteManifest() {
    // module:home
    RoutePage.Home.run {
        tabComposable(RoutePage.Home.route, tabDefaultRoutes, tabZoomRoutes) {
            HomePage()
        }
    }

    // module:square
    RoutePage.Square.run {
        tabComposable(RoutePage.Square.route, tabDefaultRoutes, tabZoomRoutes) {
            SquarePage()
        }
        themeComposable(RoutePage.Square.MyShare.route) {
            MySharePage()
        }
        themeComposable(RoutePage.Square.CreateShare.route,
            enterTransition = { enterZoom() },
            popExitTransition = { popExitAlphaHide() }) {
            CreateSharePage()
        }
    }

    // module:system
    RoutePage.System.run {
        tabComposable(RoutePage.System.route, tabDefaultRoutes, tabZoomRoutes) {
            SystemPage()
        }
        themeComposable(RoutePage.System.SystemContentTab.route,
            enterTransition = { enterZoom() },
            popExitTransition = { popExitAlphaHide() }) {
            val parentTab =
                WeakDataHolder.instance.getData<ParentTab>(RouteParamsKey.tabDataKey)
            SystemContentTabPage(parentTab)
        }
    }

    // module:me
    RoutePage.Me.run {
        tabComposable(RoutePage.Me.route, tabDefaultRoutes, tabZoomRoutes) {
            MePage()
        }
        themeComposable(RoutePage.Me.Coin.route) {
            CoinPage()
        }
        themeComposable(RoutePage.Me.CoinRank.route) {
            CoinRankPage()
        }
        themeComposable(RoutePage.Me.Collect.route) {
            CollectPage()
        }
        themeComposable(RoutePage.Me.Settings.route) {
            SettingsPage()
        }
    }

    // module:todoModel
    RoutePage.Todo.run {
        themeComposable(RoutePage.Todo.route,
            enterTransition = { enterZoom() },
            popExitTransition = { popExitAlphaHide() }) {
            TodoPage()
        }
        themeComposable(RoutePage.Todo.CreateTodo.route) {
            CreateTodoPage()
        }
        themeComposable(
            RoutePage.Todo.CreateTodo.parseRoute(),
            arguments = RoutePage.Todo.CreateTodo.parseArguments()
        ) { entry ->
            val detailsJson = entry.arguments?.getString(RouteParamsKey.todoDataKey)
            val todoData = HttpManager.getGson().fromJson(detailsJson, TodoData::class.java)
            CreateTodoPage(todoData = todoData)
        }
    }

    // module:official
    RoutePage.Official.run {
        themeComposable(RoutePage.Official.route,
            enterTransition = { enterZoom() },
            popExitTransition = { popExitAlphaHide() }) {
            OfficialPage()
        }
    }

    // module:project
    RoutePage.Project.run {
        themeComposable(RoutePage.Project.route,
            enterTransition = { enterZoom() },
            popExitTransition = { popExitAlphaHide() }) {
            ProjectPage()
        }
    }

    // module:search
    RoutePage.Search.run {
        themeComposable(RoutePage.Search.route,
            enterTransition = { enterZoom() },
            popExitTransition = { popExitAlphaHide() }) {
            SearchPage()
        }
        themeComposable(
            route = RoutePage.Search.SearchResult.parseRoute(),
            arguments = RoutePage.Search.SearchResult.parseArguments()
        ) { entry ->
            val searchKey = entry.arguments?.getString(RouteParamsKey.searchKey)
            SearchResultPage(searchKey = searchKey ?: "")
        }
    }

    // module:details
    RoutePage.Details.run {
        themeComposable(
            route = RoutePage.Details.parseRoute(),
            arguments = RoutePage.Details.parseArguments()
        ) { entry ->
            val detailsJson = entry.arguments?.getString(RouteParamsKey.detailsDataKey)
            val details =
                HttpManager.getGson().fromJson(detailsJson, DetailsData::class.java)
            DetailsPage(details)
        }
    }

    // module:account
    RoutePage.Account.run {
        themeComposable(route = RoutePage.Account.Login.route) {
            LoginPage()
        }
        themeComposable(route = RoutePage.Account.Register.route) {
            RegisterPage()
        }
    }
}