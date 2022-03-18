package com.lee.playcompose.search

import android.app.Application
import com.google.auto.service.AutoService
import com.lee.playcompose.service.ApplicationService

@AutoService(ApplicationService::class)
class SearchApplication : ApplicationService {
    override fun init(application: Application) {
//        SearchHistoryDatabase.getInstance(application)
    }
}