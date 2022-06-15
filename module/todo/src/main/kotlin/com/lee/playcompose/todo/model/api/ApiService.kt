package com.lee.playcompose.todo.model.api

import com.lee.playcompose.todo.model.entity.TodoType
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.entity.PageData
import com.lee.playcompose.common.entity.TodoData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * todo模块api接口
 * @author jv.lee
 * @date 2021/12/24
 */
interface ApiService {

    /**
     * 新增一个TODO
     * @param title 新增标题（必须）
     * @param content 新增详情（必须）
     * @param date 2018-08-01 预定完成时间（不传默认当天，建议传）
     * @param type 工作:1、生活:2、娱乐:3 如果不设置type则为 0，未来无法做 type=0的筛选，会显示全部（筛选 type 必须为大于 0 的整数）（可选）
     * @param priority priority 主要用于定义优先级，在app 中预定义几个优先级：一般:0 重要:1（可选）
     */
    @POST("/lg/todo/add/json")
    @FormUrlEncoded
    suspend fun postAddTodoAsync(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("date") date: String,
        @Field("type") @TodoType type: Int = 0,
        @Field("priority") priority: Int = 2
    ): Data<TodoData>

    /**
     * 删除一个TODO
     * @param id todoID
     */
    @POST("lg/todo/delete/{id}/json")
    suspend fun postDeleteTodoAsync(@Path("id") id: Long): Data<Any>

    /**
     * 更新一个TODO
     * @param id todoID
     * @param title 新增标题（必须）
     * @param content 新增详情（必须）
     * @param date 2018-08-01 预定完成时间（不传默认当天，建议传）
     * @param type 工作:1、生活:2、娱乐:3 如果不设置type则为 0，未来无法做 type=0的筛选，会显示全部（筛选 type 必须为大于 0 的整数）（可选）
     * @param priority priority 主要用于定义优先级，在app 中预定义几个优先级：一般:0 重要:1（可选）
     * @param status 0为未完成，1为完成
     */
    @POST("/lg/todo/update/{id}/json")
    @FormUrlEncoded
    suspend fun postUpdateTodoAsync(
        @Path("id") id: Long,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("date") date: String,
        @Field("type") @TodoType type: Int = 0,
        @Field("priority") priority: Int = 2,
        @Field("status") status: Int
    ): Data<TodoData>

    /**
     * 仅更新完成状态TODO
     * @param id todoID
     * @param status 0为未完成，1为完成
     */
    @POST("/lg/todo/done/{id}/json")
    @FormUrlEncoded
    suspend fun postUpdateTodoStatusAsync(
        @Path("id") id: Long,
        @Field("status") status: Int
    ): Data<Any>

    /**
     * 获取TODO列表
     * @param page 页码从1开始
     * @param status 状态， 1-完成；0未完成; 默认全部展示；
     * @param type 创建时传入的类型, 默认全部展示
     * @unParam priority 创建时传入的优先级；默认全部展示
     * @unParam order 1:完成日期顺序；2.完成日期逆序；3.创建日期顺序；4.创建日期逆序(默认)；
     */
    @POST("/lg/todo/v2/list/{page}/json")
    @FormUrlEncoded
    suspend fun postTodoDataAsync(
        @Path("page") page: Int,
        @Field("type") @TodoType type: Int = 0,
        @Field("status") status: Int
    ): Data<PageData<TodoData>>


}