package com.lee.playcompose.base.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * @author jv.lee
 * @date 2022/3/22
 * @description
 */

fun Modifier.onTap(action: () -> Unit) = pointerInput(Unit) {
    detectTapGestures(onTap = { action() })
}