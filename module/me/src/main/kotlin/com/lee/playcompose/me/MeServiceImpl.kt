package com.lee.playcompose.me

import com.google.auto.service.AutoService
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.ktx.createApi
import com.lee.playcompose.me.model.api.ApiService
import com.lee.playcompose.service.MeService

/**
 * 我的模块功能对外服务实现
 * @author jv.lee
 * @date 2022/3/30
 */
@AutoService(MeService::class)
class MeServiceImpl : MeService {
    override suspend fun requestCollectAsync(id: Long): Data<String> {
        return createApi<ApiService>().postCollectAsync(id)
    }
}