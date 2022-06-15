package com.lee.playcompose.search

import android.app.Application
import com.google.auto.service.AutoService
import com.lee.playcompose.search.model.db.SearchDatabase
import com.lee.playcompose.service.core.ApplicationService

/**
 * 搜索模块application初始化实现
 * @author jv.lee
 * @date 2021/11/22
 */
@AutoService(ApplicationService::class)
class SearchApplication : ApplicationService {
    override fun init(application: Application) {
        SearchDatabase.getInstance(application)
    }
}