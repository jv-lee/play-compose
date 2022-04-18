package com.lee.playcompose.base.extensions

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/22
 * @description compose扩展函数工具类
 */

/**
 * 无状态点击 （点击后没有android水波纹效果）
 */
fun Modifier.onTap(action: () -> Unit) = pointerInput(Unit) {
    detectTapGestures(onTap = { action() })
}

/**
 * 监听软键盘是否弹开
 */
fun PaddingValues.hasBottomExpend(expend: () -> Unit, close: () -> Unit) {
    if (calculateBottomPadding() > 0.dp) {
        expend()
    } else {
        close()
    }
}

/**
 * 获取键盘弹起padding监听值
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

/**
 * 延迟状态数据获取
 * @param default 默认
 * @param update 更改
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun <T> delayState(default: T, update: T): MutableState<T> {
    val scope = rememberCoroutineScope()
    val progressColor = remember { mutableStateOf(default) }
    scope.launch {
        delay(100)
        progressColor.value = update
    }
    return progressColor
}

/**
 * viewModel存储至activity 与activity生命全周期绑定 (作用app全局viewModel使用)
 */
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