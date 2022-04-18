package com.lee.playcompose.search.model.entity

import androidx.compose.ui.graphics.Color
import java.util.*

/**
 * @author jv.lee
 * @date 2021/11/4
 * @description 搜索热词 提供本地数据源
 */
data class SearchHot(
    val key: String,
    val color: Color = randomColor()
) {
    companion object {
        /**
         * 提供热门搜索词条
         */
        fun getHotCategory() = arrayListOf(
            SearchHot("MVVM"),
            SearchHot("面试"),
            SearchHot("gradle"),
            SearchHot("动画"),
            SearchHot("CameraX"),
            SearchHot("自定义View"),
            SearchHot("性能优化"),
            SearchHot("Jetpack"),
            SearchHot("Kotlin"),
            SearchHot("Flutter"),
            SearchHot("OpenGL"),
            SearchHot("FFmpeg")
        )
    }
}

private fun randomColor(): Color {
    val random = Random()
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
}