package com.lee.playcompose.common.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * @author jv.lee
 * @date 2020/3/25
 * @description 网络请求操作类
 */
@Keep
data class Data<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String
)

@Keep
data class PageData<T>(
    @SerializedName("datas")
    val data: MutableList<T>,
    val curPage: Int,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int,
)

/**
 * BannerAPI data<T>
 */
@Keep
data class Banner(
    val id: Long,
    val title: String,
    val desc: String,
    val imagePath: String,
    val url: String,
    val isVisible: Int,
    val order: Int,
    val type: Int,
    val collect: Boolean = false
)

/**
 * 分页具体数据列表 data<T>
 * 最热数据列表 data<T>
 * 搜索数据列表 data<T>
 */
@Keep
data class Content(
    val apkLink: String,
    val audit: Int,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Long,
    val originId: Long,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Long,
    val superChapterName: String,
    val tags: List<Tag>,
    val title: String,
    val type: Int,
    val userId: Long,
    val visible: Int,
    val zan: Long,
    val top: String
)

@Keep
data class Tag(
    val name: String,
    val url: String
)

@Parcelize
@Keep
data class Tab(
    val id: Long,
    val courseId: Long,
    val name: String,
    val order: Long,
    val parentChapterId: Long,
    val userControlSetTop: Boolean,
    val visible: Int
) : Parcelable

@Parcelize
@Keep
data class ParentTab(
    val id: Long,
    val courseId: Long,
    val name: String,
    val order: Long,
    val parentChapterId: Long,
    val userControlSetTop: Boolean,
    val visible: Int,
    val children: List<Tab>
) :Parcelable

@Keep
data class NavigationItem(
    val cid: Long,
    val name: String,
    val articles: List<Content>
)

@Keep
data class CoinRecord(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Long,
    val reason: String,
    val type: Int,
    val userId: Long,
    val userName: String
)

@Keep
data class CoinRank(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: String,
    val userId: Long,
    val username: String
)

@Keep
data class MyShareData(
    val coinInfo: CoinInfo,
    val shareArticles: PageData<Content>
)

@Parcelize
@Keep
data class TodoData(
    val id: Long,
    val userId: Int,
    val completeDate: String,
    val completeDateStr: String,
    var content: String,
    var date: Long,
    var dateStr: String,
    var status: Int,
    var title: String,
    var type: Int,
    var priority: Int
) : Parcelable {
    companion object {
        // 0：一般 1：重要
        const val PRIORITY_LOW = 0
        const val PRIORITY_HEIGHT = 1
    }
}