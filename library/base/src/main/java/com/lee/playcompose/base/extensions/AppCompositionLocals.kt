package com.lee.playcompose.base.extensions

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.FragmentActivity

/**
 * @author jv.lee
 * @date 2022/3/18
 * @description
 */

val LocalActivity = staticCompositionLocalOf<FragmentActivity> {
    noLocalProvidedFor("LocalActivity")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}