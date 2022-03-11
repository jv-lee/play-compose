package com.lee.playcompose.router

import android.os.Parcelable

object ParamsKey {
    const val detailsDataKey = "detailsData"
}

sealed class PageRoute(val route: String, val paramsKey: Map<String, Any> = emptyMap()) {
    object Home : PageRoute("Home")
    object Square : PageRoute("Square")
    object System : PageRoute("System")
    object Me : PageRoute("Me")
    object Official : PageRoute("Official")
    object Project : PageRoute("Project")

    object Details : PageRoute(
        "Details",
        paramsKey = mapOf<String, Class<*>>(
            Pair(ParamsKey.detailsDataKey, Parcelable::class.java)
        )
    )
}