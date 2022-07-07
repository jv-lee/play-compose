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

/**
 * app路由配置 (页面路由注册)
 * @author jv.lee
 * @date 2022/3/16
 */

fun NavGraphBuilder.appRouteManifest() {
    // module:home
    RoutePage.Home.run {
        tabComposable(RoutePage.Home.route) {
            HomePage()
        }
    }

    // module:square
    RoutePage.Square.run {
        tabComposable(RoutePage.Square.route) {
            SquarePage()
        }
        sideComposable(RoutePage.Square.MyShare.route) {
            MySharePage()
        }
        sideComposable(RoutePage.Square.CreateShare.route) {
            CreateSharePage()
        }
    }

    // module:system
    RoutePage.System.run {
        tabComposable(RoutePage.System.route) {
            SystemPage()
        }
        sideComposable(RoutePage.System.SystemContentTab.route) {
            val parentTab =
                WeakDataHolder.instance.getData<ParentTab>(RouteParamsKey.tabDataKey)
            SystemContentTabPage(parentTab)
        }
    }

    // module:me
    RoutePage.Me.run {
        tabComposable(RoutePage.Me.route) {
            MePage()
        }
        sideComposable(RoutePage.Me.Coin.route) {
            CoinPage()
        }
        sideComposable(RoutePage.Me.CoinRank.route) {
            CoinRankPage()
        }
        sideComposable(RoutePage.Me.Collect.route) {
            CollectPage()
        }
        sideComposable(RoutePage.Me.Settings.route) {
            SettingsPage()
        }
    }

    // module:todoModel
    RoutePage.Todo.run {
        sideComposable(RoutePage.Todo.route) {
            TodoPage()
        }
        sideComposable(RoutePage.Todo.CreateTodo.route) {
            CreateTodoPage()
        }
        sideComposable(
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
        sideComposable(RoutePage.Official.route) {
            OfficialPage()
        }
    }

    // module:project
    RoutePage.Project.run {
        sideComposable(RoutePage.Project.route) {
            ProjectPage()
        }
    }

    // module:search
    RoutePage.Search.run {
        sideComposable(RoutePage.Search.route) {
            SearchPage()
        }
        sideComposable(
            route = RoutePage.Search.SearchResult.parseRoute(),
            arguments = RoutePage.Search.SearchResult.parseArguments()
        ) { entry ->
            val searchKey = entry.arguments?.getString(RouteParamsKey.searchKey)
            SearchResultPage(searchKey = searchKey ?: "")
        }
    }

    // module:details
    RoutePage.Details.run {
        sideComposable(
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
        sideComposable(route = RoutePage.Account.Login.route) {
            LoginPage()
        }
        sideComposable(route = RoutePage.Account.Register.route) {
            RegisterPage()
        }
    }
}