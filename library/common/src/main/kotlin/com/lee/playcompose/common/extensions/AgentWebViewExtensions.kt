/*
 * WebView扩展函数
 * @author jv.lee
 * @date 2021/11/18
 */
package com.lee.playcompose.common.extensions

import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.just.agentweb.AgentWeb
import com.lee.playcompose.base.tools.DarkModeTools

/**
 * AgentWebView 绑定生命周期控制生命状态
 */
fun AgentWeb.bindLifecycle(lifecycle: Lifecycle): AgentWeb {
    lifecycle.addObserver(object : LifecycleEventObserver {
        var activeEvent: Lifecycle.Event? = null
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (activeEvent == Lifecycle.Event.ON_PAUSE) return
                    activeEvent = event
                    webLifeCycle.onPause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (activeEvent == Lifecycle.Event.ON_RESUME) return
                    activeEvent = event
                    webLifeCycle.onResume()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    activeEvent = event
                    webLifeCycle.onDestroy()
                    lifecycle.removeObserver(this)
                }
                else -> {
                }
            }
        }
    })
    return this
}

/**
 * webView设置back事件拦截
 */
fun WebView.setWebBackEvent() {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    setOnKeyListener(object : View.OnKeyListener {
        override fun onKey(view: View?, i: Int, keyEvent: KeyEvent?): Boolean {
            if (canGoBack()) {
                goBack()
                return true
            }
            return false
        }

    })
}

/**
 * webView适配深色模式
 */
fun AgentWeb.supportDarkMode(): AgentWeb {
    val settings = this.agentWebSettings.webSettings

    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
        if (DarkModeTools.get().isDark) {
            WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
        } else {
            WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF)
        }
    }
    return this
}