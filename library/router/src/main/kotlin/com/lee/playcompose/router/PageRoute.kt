package com.lee.playcompose.router

sealed class PageRoute(val route: String) {
    object Main : PageRoute("Main")
    object Home : PageRoute("Home")
    object Square : PageRoute("Square")
    object System : PageRoute("System")
    object Me : PageRoute("Me")
    object Details : PageRoute("Details")
}