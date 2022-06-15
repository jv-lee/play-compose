package com.lee.playcompose.system.model.api

import com.lee.playcompose.common.entity.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 体系模块api接口
 * @author jv.lee
 * @date 2021/11/8
 */
interface ApiService {

    @GET("tree/json")
    suspend fun getParentTabAsync(): Data<List<ParentTab>>

    /**
     * @param page 分页页面 取值[0-40]
     */
    @GET("article/list/{page}/json")
    suspend fun getContentDataAsync(
        @Path("page") page: Int,
        @Query("cid") id: Long
    ): Data<PageData<Content>>

    @GET("navi/json")
    suspend fun getNavigationDataAsync(): Data<List<NavigationItem>>
}