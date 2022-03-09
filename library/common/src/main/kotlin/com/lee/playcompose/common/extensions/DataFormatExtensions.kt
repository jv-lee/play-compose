package com.lee.playcompose.common.extensions

import androidx.core.text.HtmlCompat
import com.lee.playcompose.base.utils.TimeUtil
import com.lee.playcompose.common.entity.Content

/**
 * @author jv.lee
 * @date 2022/3/2
 * @description 公共数据转换扩展函数
 */

fun Content.getTitle(): String =
    HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

fun Content.getAuthor(): String = author.ifEmpty { shareUser }

fun Content.getDateFormat(): String = TimeUtil.getChineseTimeMill(publishTime)

fun Content.getCategory(): String {
    return when {
        superChapterName.isNotEmpty() and chapterName.isNotEmpty() -> "$superChapterName / $chapterName"
        superChapterName.isNotEmpty() -> superChapterName
        chapterName.isNotEmpty() -> chapterName
        else -> ""
    }
}