package com.lee.playcompose.base.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.app.ComponentActivity

/**
 * @author jv.lee
 * @date 2022/3/18
 * @description
 */

fun Modifier.onTap(action: () -> Unit) = pointerInput(Unit) {
    detectTapGestures(onTap = { action() })
}

val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    noLocalProviderFor("LocalActivity")
}

private fun noLocalProviderFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}