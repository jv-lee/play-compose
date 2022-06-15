package com.lee.playcompose.search.model.api

import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.entity.PageData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 搜索模块api接口
 * @author jv.lee
 * @date 2021/11/19
 */
interface ApiService {

    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun postSearchAsync(
        @Path("page") page: Int,
        @Field("k") key: String
    ): Data<PageData<Content>>

}