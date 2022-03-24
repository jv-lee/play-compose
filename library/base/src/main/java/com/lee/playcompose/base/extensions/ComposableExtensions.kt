package com.lee.playcompose.base.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * @author jv.lee
 * @date 2022/3/22
 * @description
 */

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