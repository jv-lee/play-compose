package com.lee.playcompose.common.extensions

import android.widget.Toast
import com.lee.playcompose.base.core.ApplicationExtensions.app

/**
 * @author jv.lee
 * @date 2022/2/24
 * @description
 */

fun toast(message: String?) {
    message ?: return
    Toast.makeText(app.applicationContext, message, Toast.LENGTH_SHORT).show()
}
