package com.lee.playcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.lee.playcompose.common.ui.theme.PlayComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            PlayComposeTheme {
                SplashNavigator {
                    RouteNavigator()
                }
            }
        }
    }

}
