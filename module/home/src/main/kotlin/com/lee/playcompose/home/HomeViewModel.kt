package com.lee.playcompose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.extensions.pager

/**
 * @author jv.lee
 * @date 2022/2/28
 * @description
 */
class HomeViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    val pager by lazy {
        pager { api.getContentDataAsync(it) }.cachedIn(viewModelScope)
    }

}