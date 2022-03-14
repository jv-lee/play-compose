package com.lee.playcompose.project.model.api

import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.entity.Tab
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description
 */
interface ApiService {

    @GET("project/tree/json")
    suspend fun getProjectTabsAsync(): Data<List<Tab>>

    /**
     * @param id 项目id 由tab接口获取
     * @param page 分页页面 取值[1-40]
     */
    @GET("project/list/{page}/json")
    suspend fun getProjectDataAsync(
        @Path("page") page: Int,
        @Query("cid") id: Long
    ): Data<PageData<Content>>
}