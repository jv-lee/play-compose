package com.lee.playcompose.router

import android.os.Parcelable

object ParamsKey {
    const val detailsDataKey = "detailsData"
    const val tabDataKey = "tabData"
    const val searchKey = "searchKey"
}

sealed class PageRoute(val route: String, val paramsKey: Map<String, Any> = emptyMap()) {
    object Home : PageRoute("Home")
    object Square : PageRoute("Square")
    object System : PageRoute("System")
    object Me : PageRoute("Me")
    object Official : PageRoute("Official")
    object Project : PageRoute("Project")
    object Search : PageRoute("Search")
    object SearchResult : PageRoute(
        "SearchResult",
        paramsKey = mapOf(Pair(ParamsKey.searchKey, String::class.java))
    )

    object CreateShare : PageRoute("CreateShare")
    object MyShare : PageRoute("MyShare")

    object SystemContentTab : PageRoute("SystemContentTab")

    object Details : PageRoute(
        "Details",
        paramsKey = mapOf<String, Class<*>>(
            Pair(ParamsKey.detailsDataKey, Parcelable::class.java)
        )
    )

    object Login : PageRoute("Login")
    object Register : PageRoute("Register")
}