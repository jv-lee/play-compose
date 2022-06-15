package com.lee.playcompose.system.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.common.entity.ParentTab
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.saved.SavedSinglePager
import com.lee.playcompose.common.paging.saved.singleSavedPager
import com.lee.playcompose.system.model.api.ApiService

/**
 * 体系内容列表 viewModel
 * @author jv.lee
 * @date 2022/3/8
 */
class SystemContentViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val savedPager by lazy {
        singleSavedPager {
            api.getParentTabAsync().checkData().filter {
                it.children.isNotEmpty()
            }
        }
    }

    var viewStates by mutableStateOf(
        SystemContentViewState(savedPager = savedPager)
    )
        private set
}

data class SystemContentViewState(
    val savedPager: SavedSinglePager<ParentTab>,
    val listState: LazyListState = LazyListState()
)