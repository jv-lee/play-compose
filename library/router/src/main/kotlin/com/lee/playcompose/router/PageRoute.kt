package com.lee.playcompose.router

sealed class PageRoute(val route: String) {
    object Main : PageRoute("Main")
    object Details : PageRoute("Details")
    object DetailsChild : PageRoute("DetailsChild")
}