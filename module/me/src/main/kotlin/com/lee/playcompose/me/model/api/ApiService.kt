package com.lee.playcompose.me.model.api

import com.lee.playcompose.common.entity.*
import retrofit2.http.*

/**
 *
 * @author jv.lee
 * @date 2021/11/22
 */
interface ApiService {

    /**
     * 获取积分信息
     */
    @GET("lg/coin/userinfo/json")
    suspend fun getCoinInfoAsync(): Data<CoinInfo>

    /**
     * 获取积分记录
     * @param page 1 - *
     */
    @GET("lg/coin/list/{page}/json")
    suspend fun getCoinRecordAsync(@Path("page") page: Int): Data<PageData<CoinRecord>>

    /**
     * 获取积分排行榜列表
     * @param page 1 - *
     */
    @GET("coin/rank/{page}/json")
    suspend fun getCoinRankAsync(@Path("page") page: Int): Data<PageData<CoinRank>>

    /**
     * 收藏文章
     * @param id 文章id
     */
    @POST("lg/collect/{id}/json")
    suspend fun postCollectAsync(@Path("id") id: Long): Data<String>

    /**
     * 取消收藏文章
     * @param id 文章id
     */
    @POST("lg/uncollect/{id}/json")
    @FormUrlEncoded
    suspend fun postUnCollectAsync(
        @Path("id") id: Long,
        @Field("originId") originId: Long
    ): Data<String>

    /**
     * 获取收藏记录
     * @param page 0 - *
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectListAsync(@Path("page") page: Int): Data<PageData<Content>>
}