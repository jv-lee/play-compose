package com.lee.playcompose.official.model.api

import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.entity.Tab
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author jv.lee
 * @date 2021/11/8
 * @description
 */
interface ApiService {

    @GET("wxarticle/chapters/json")
    suspend fun getOfficialTabsAsync(): Data<List<Tab>>

    /**
     * @param id 公众号id 由tab接口获取
     * @param page 分页页面 取值[0-40]
     */
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getOfficialDataAsync(
        @Path("id") id: Long,
        @Path("page") page: Int
    ): Data<PageData<Content>>
}