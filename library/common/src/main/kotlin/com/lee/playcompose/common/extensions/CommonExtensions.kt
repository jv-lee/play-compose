package com.lee.playcompose.common.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*

/**
 * @author jv.lee
 * @date 2022/3/18
 * @description
 */

fun randomColor(): Color {
    val random = Random()
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
}

fun PaddingValues.hasBottomExpend(expend: () -> Unit, close: () -> Unit) {
    if (calculateBottomPadding() > 0.dp) {
        expend()
    } else {
        close()
    }
}