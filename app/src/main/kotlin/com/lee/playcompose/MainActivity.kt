package com.lee.playcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lee.playcompose.common.extensions.SimpleAnimatedNavHost
import com.lee.playcompose.common.extensions.sideComposable
import com.lee.playcompose.common.ui.theme.PlayComposeTheme
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.ui.page.MainPage
import com.lee.playcompose.ui.page.SplashPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets {
                PlayComposeTheme {
                    AppContent()
                }
            }
        }
    }

    @Composable
    fun AppContent() {
        var isSplash by remember { mutableStateOf(true) }
        if (isSplash) SplashPage { isSplash = false } else Navigator()
    }

    @ExperimentalAnimationApi
    @Composable
    fun Navigator() {
        val navController = rememberAnimatedNavController()
        Scaffold(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            content = {
                SimpleAnimatedNavHost(
                    navController = navController,
                    startDestination = PageRoute.Main.route,
                ) {
                    sideComposable(PageRoute.Main.route) { MainPage(navController) }
                }
            })
    }

}
