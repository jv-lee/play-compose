package com.lee.playcompose.me

import com.google.auto.service.AutoService
import com.lee.playcompose.common.entity.Data
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.me.model.api.ApiService
import com.lee.playcompose.service.MeService

/**
 * @author jv.lee
 * @date 2022/3/30
 * @description
 */
@AutoService(MeService::class)
class MeServiceImpl : MeService {
    override suspend fun requestCollectAsync(id: Long): Data<String> {
        return createApi<ApiService>().postCollectAsync(id)
    }
}