package com.lee.playcompose.service

import com.lee.playcompose.common.entity.Data

/**
 * 账户模块对外提供功能服务类
 * @author jv.lee
 * @date 2021/12/3
 */
interface MeService {

    /**
     * 我的模块发起收藏网络请求
     * @param id 文章id
     */
    suspend fun requestCollectAsync(id: Long): Data<String>
}