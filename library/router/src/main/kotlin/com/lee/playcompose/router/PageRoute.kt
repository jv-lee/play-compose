package com.lee.playcompose.router

import android.os.Parcelable

/**
 * 全局路由参数key
 */
object ParamsKey {
    const val detailsDataKey = "detailsData"
    const val tabDataKey = "tabData"
    const val searchKey = "searchKey"
}

/**
 *  全局页面路由配置
 */
sealed class PageRoute(val route: String, val paramsKey: Map<String, Any> = emptyMap()) {
    /**
     * module:home
     */
    object Home : PageRoute("Home")

    /**
     * module:square
     */
    object Square : PageRoute("Square") {
        object MyShare : PageRoute("MyShare")
        object CreateShare : PageRoute("CreateShare")
    }

    /**
     * module:system
     */
    object System : PageRoute("System") {
        object SystemContentTab : PageRoute("SystemContentTab")
    }

    /**
     * module:me
     */
    object Me : PageRoute("Me") {
        object Coin : PageRoute("Coin")
        object CoinRank : PageRoute("CoinRank")
        object Collect : PageRoute("Collect")
        object Settings : PageRoute("Settings")
    }

    /**
     * module:todoModel
     */
    object Todo : PageRoute("Todo")

    /**
     * module:official
     */
    object Official : PageRoute("Official")

    /**
     * module:project
     */
    object Project : PageRoute("Project")

    /**
     * module:search
     */
    object Search : PageRoute("Search") {
        object SearchResult : PageRoute(
            "SearchResult",
            paramsKey = mapOf(Pair(ParamsKey.searchKey, String::class.java))
        )
    }

    /**
     * module:details
     */
    object Details : PageRoute(
        "Details",
        paramsKey = mapOf<String, Class<*>>(
            Pair(ParamsKey.detailsDataKey, Parcelable::class.java)
        )
    )

    /**
     * module:account
     */
    object Account {
        object Login : PageRoute("Login")
        object Register : PageRoute("Register")
    }
}