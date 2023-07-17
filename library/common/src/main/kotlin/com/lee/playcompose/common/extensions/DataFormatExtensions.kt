/*
 * 全局数据实体转换扩展函数
 * @author jv.lee
 * @date 2022/3/2
 */
package com.lee.playcompose.common.extensions

import androidx.core.text.HtmlCompat
import com.lee.playcompose.base.utils.TimeUtil
import com.lee.playcompose.common.entity.*

fun Content.getTitle(): String =
    HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

fun Content.getDescription(): String =
    HtmlCompat.fromHtml(desc, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

fun Content.getAuthor(): String = author.ifEmpty { shareUser }

fun Content.getDateFormat(): String = TimeUtil.getChineseTimeMill(publishTime)

fun Content.getCategory(): String {
    return when {
        superChapterName.isNotEmpty()
            and chapterName.isNotEmpty() -> "$superChapterName / $chapterName"
        superChapterName.isNotEmpty() -> superChapterName
        chapterName.isNotEmpty() -> chapterName
        else -> ""
    }
}

fun Content.transformDetails(): DetailsData =
    DetailsData(id = id.toString(), title = getTitle(), link = link, isCollect = collect)

fun Banner.transformDetails(): DetailsData =
    DetailsData(id = id.toString(), title = title, link = url, isCollect = collect)

fun ParentTab.formHtmlLabels(): String {
    return HtmlCompat.fromHtml(
        children.buildChildrenLabel(),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
}

private fun List<Tab>.buildChildrenLabel(): String {
    val builder = StringBuilder()
    forEach {
        builder.append(it.name + "\t\t")
    }
    return builder.toString()
}