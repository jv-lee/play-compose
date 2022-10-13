package com.lee.playcompose.search.model.entity

import androidx.compose.ui.graphics.Color
import java.util.*

/**
 * 搜索热词 提供本地数据源
 * @author jv.lee
 * @date 2021/11/4
 */
data class SearchHotUI(
    val key: String,
    val color: Color = randomColor()
) {
    companion object {
        /**
         * 提供热门搜索词条
         */
        fun getHotCategory() = arrayListOf(
            SearchHotUI("MVVM"),
            SearchHotUI("面试"),
            SearchHotUI("gradle"),
            SearchHotUI("动画"),
            SearchHotUI("CameraX"),
            SearchHotUI("自定义View"),
            SearchHotUI("性能优化"),
            SearchHotUI("Jetpack"),
            SearchHotUI("Kotlin"),
            SearchHotUI("Flutter"),
            SearchHotUI("OpenGL"),
            SearchHotUI("FFmpeg")
        )
    }
}

private fun randomColor(): Color {
    val random = Random()
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
}