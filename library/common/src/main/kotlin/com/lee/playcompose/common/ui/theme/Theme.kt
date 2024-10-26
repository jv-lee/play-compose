package com.lee.playcompose.common.ui.theme

import androidx.activity.enableEdgeToEdge
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.lee.playcompose.base.extensions.LocalActivity
import com.lee.playcompose.base.extensions.ProviderActivity
import com.lee.playcompose.base.extensions.ProviderNavController
import com.lee.playcompose.base.extensions.ProviderOverScroll
import com.lee.playcompose.base.extensions.activityViewModel
import com.lee.playcompose.common.extensions.agentWebPreload
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
        // 预加载webView，提升首次webView加载速度
        agentWebPreload()
        // 开启全屏内容填充
        enableEdgeToEdge()

        ColorsThemeProvider(isNightMode = viewState.isDark) {
            // 设置statusBar/navigationBar颜色
            val window = LocalActivity.current.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = viewState.statusBarDarkContentEnabled
            window.navigationBarColor = ColorsTheme.colors.window.toArgb()

            FontSizeThemeProvider(scale = viewState.fontScale) {
                ProviderNavController {
                    ProviderOverScroll {
                        MaterialTheme(
                            typography = Typography,
                            shapes = Shapes,
                            content = content
                        )
                    }
                }
            }
        }
    }
}