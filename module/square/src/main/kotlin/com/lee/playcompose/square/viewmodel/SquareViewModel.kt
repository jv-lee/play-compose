package com.lee.playcompose.square.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.common.entity.Content
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService
import com.lee.playcompose.square.model.api.ApiService

/**
 * @author jv.lee
 * @date 2022/3/3
 * @description 广场页 viewModel
 */
class SquareViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    val accountService: AccountService = ModuleService.find()

    private val pager by lazy {
        savedPager { api.getSquareDataSync(it).checkData() }
    }

    var viewStates by mutableStateOf(SquareViewState(savedPager = pager))
        private set
}

data class SquareViewState(
    val savedPager: SavedPager<Content>,
    val listState: LazyListState = LazyListState()
)