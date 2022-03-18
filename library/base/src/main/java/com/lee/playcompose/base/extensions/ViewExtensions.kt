package com.lee.playcompose.base.extensions

import android.view.ViewGroup

/**
 * @author jv.lee
 * @date 2022/3/18
 * @description
 */
/**
 * 扩展容器类设置Marin方法
 */
fun ViewGroup.setMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        (layoutParams as ViewGroup.MarginLayoutParams).run {
            setMargins(
                if (left == 0) leftMargin else left,
                if (top == 0) topMargin else top,
                if (right == 0) rightMargin else right,
                if (bottom == 0) bottomMargin else bottom
            )
        }
    }
}

