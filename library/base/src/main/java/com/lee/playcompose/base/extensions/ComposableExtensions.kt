package com.lee.playcompose.base.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

/**
 * @author jv.lee
 * @date 2022/3/22
 * @description compose扩展函数工具类
 */

@Composable
fun rememberImePaddingValue(
    imeInsets: PaddingValues = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.ime),
    navigationInserts: PaddingValues = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)
): Float {
    var paddingValue by remember { mutableStateOf(0f) }
    val imeBottom = imeInsets.calculateBottomPadding()
    val navigationBottom = navigationInserts.calculateBottomPadding()
    paddingValue = when {
        imeBottom > navigationBottom -> {
            imeBottom.value - navigationBottom.value
        }
        imeBottom.value == 0f -> {
            paddingValue
        }
        else -> {
            0f
        }
    }
    return paddingValue
}

fun Modifier.onTap(action: () -> Unit) = pointerInput(Unit) {
    detectTapGestures(onTap = { action() })
}

@Suppress("MissingJvmstatic")
@Composable
inline fun <reified VM : ViewModel> activityViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalActivity.current) {
        "No ViewModelStoreOwner was provided via LocalActivity"
    },
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): VM = androidx.lifecycle.viewmodel.compose.viewModel(
    VM::class.java,
    viewModelStoreOwner,
    key,
    factory
)