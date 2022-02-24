package com.lee.playcompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.common.ui.theme.PlayComposeTheme
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
        if (isSplash) SplashPage { isSplash = false } else RouteNavigator {
            finish()
        }
    }


}
