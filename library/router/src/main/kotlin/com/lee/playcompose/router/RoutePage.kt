package com.lee.playcompose.router

import android.os.Parcelable

/**
 * 全局路由参数key
 */
object RouteParamsKey {
    const val detailsDataKey = "detailsData"
    const val tabDataKey = "tabData"
    const val searchKey = "searchKey"
}

/**
 *  全局页面路由配置
 */
sealed class RoutePage(val route: String, val paramsKey: Map<String, Any> = emptyMap()) {
    /**
     * module:home
     */
    object Home : RoutePage("Home")

    /**
     * module:square
     */
    object Square : RoutePage("Square") {
        object MyShare : RoutePage("MyShare")
        object CreateShare : RoutePage("CreateShare")
    }

    /**
     * module:system
     */
    object System : RoutePage("System") {
        object SystemContentTab : RoutePage("SystemContentTab")
    }

    /**
     * module:me
     */
    object Me : RoutePage("Me") {
        object Coin : RoutePage("Coin")
        object CoinRank : RoutePage("CoinRank")
        object Collect : RoutePage("Collect")
        object Settings : RoutePage("Settings")
    }

    /**
     * module:todoModel
     */
    object Todo : RoutePage("Todo")

    /**
     * module:official
     */
    object Official : RoutePage("Official")

    /**
     * module:project
     */
    object Project : RoutePage("Project")

    /**
     * module:search
     */
    object Search : RoutePage("Search") {
        object SearchResult : RoutePage(
            "SearchResult",
            paramsKey = mapOf(Pair(RouteParamsKey.searchKey, String::class.java))
        )
    }

    /**
     * module:details
     */
    object Details : RoutePage(
        "Details",
        paramsKey = mapOf<String, Class<*>>(
            Pair(RouteParamsKey.detailsDataKey, Parcelable::class.java)
        )
    )

    /**
     * module:account
     */
    object Account {
        object Login : RoutePage("Login")
        object Register : RoutePage("Register")
    }
}