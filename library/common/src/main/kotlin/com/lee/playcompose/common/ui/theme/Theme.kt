package com.lee.playcompose.common.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lee.playcompose.base.extensions.ProviderActivity
import com.lee.playcompose.base.extensions.ProviderNavController
import com.lee.playcompose.base.extensions.ProviderOverScroll
import com.lee.playcompose.base.extensions.activityViewModel
import com.lee.playcompose.common.viewmodel.ThemeViewModel

@Composable
fun FragmentActivity.PlayComposeTheme(
    viewModel: ThemeViewModel = activityViewModel(
        this,
        factory = ThemeViewModel.ViewModelFactory(this)
    ),
    content: @Composable () -> Unit
) {
    val viewState = viewModel.viewStates

    ProviderActivity {
        ColorsThemeProvider(isNightMode = viewState.isDark) {
            // 动态设置 statusBar/navigationBar color.
            val systemUiController = rememberSystemUiController()
            systemUiController.statusBarDarkContentEnabled = viewState.statusBarDarkContentEnabled
            systemUiController.setNavigationBarColor(ColorsTheme.colors.window)

            val typography = Typography(
                body1 = TextStyle(
                    color = ColorsTheme.colors.accent,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )

            FontSizeThemeProvider(scale = viewState.fontScale) {
                ProviderNavController {
                    ProviderOverScroll {
                        MaterialTheme(typography = typography, shapes = Shapes, content = content)
                    }
                }
            }
        }
    }
}