/*
 * compose扩展函数工具类
 * @author jv.lee
 * @date 2022/3/22
 */
package com.lee.playcompose.base.extensions

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
 * fade模式动画显示组件
 */
@Composable
fun FadeAnimatedVisibility(
    modifier: Modifier = Modifier,
    visible: Boolean? = null,
    visibleState: MutableTransitionState<Boolean>? = null,
    enter: EnterTransition = fadeIn(animationSpec = TweenSpec(600)),
    exit: ExitTransition = fadeOut(animationSpec = TweenSpec(600)),
    label: String = "FadeAnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    visible?.run {
        AnimatedVisibility(visible, modifier, enter, exit, label, content)
    }

    visibleState?.run {
        AnimatedVisibility(visibleState, modifier, enter, exit, label, content)
    }
}

@Composable
fun BottomSlideAnimatedVisible(
    modifier: Modifier = Modifier,
    visible: Boolean? = null,
    visibleState: MutableTransitionState<Boolean>? = null,
    enter: EnterTransition = slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = TweenSpec(600)
    ),
    exit: ExitTransition = slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight * 2 },
        animationSpec = TweenSpec(600)
    ),
    label: String = "BottomSlideAnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    visible?.run {
        AnimatedVisibility(visible, modifier, enter, exit, label, content)
    }

    visibleState?.run {
        AnimatedVisibility(visibleState, modifier, enter, exit, label, content)
    }
}

/**
 * 获取键盘弹起padding监听值
 */
@Composable
fun rememberImePaddingValue(
    imeInsets: PaddingValues = WindowInsets.ime.asPaddingValues(),
    navigationInserts: PaddingValues = WindowInsets.navigationBars.asPaddingValues()
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
    activity: FragmentActivity? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(activity ?: LocalActivity.current) {
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

@Composable
fun OnLifecycleEvent(onEvent: (event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(newValue = onEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            eventHandler.value(event)
        }
        lifecycle.addObserver(observer)

        onDispose { lifecycle.removeObserver(observer) }
    }
}

/**
 * 监听屏幕宽高compose组件
 * @param content width:像素值 height:像素值
 */
@Composable
fun ScreenSizeChange(content: @Composable (width: Float, height: Float) -> Unit) {
    val configuration = LocalConfiguration.current
    remember(configuration) { configuration.orientation }

    val context = LocalContext.current
    val density = LocalDensity.current
    val viewWidth = context.resources.displayMetrics.widthPixels / density.density
    val viewHeight = context.resources.displayMetrics.heightPixels / density.density

    content(viewWidth, viewHeight)
}