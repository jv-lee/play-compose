package com.lee.playcompose.me.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.CoinRecord
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.ktx.checkData
import com.lee.playcompose.common.ktx.createApi
import com.lee.playcompose.common.paging.saved.SavedPager
import com.lee.playcompose.common.paging.saved.savedPager
import com.lee.playcompose.me.R
import com.lee.playcompose.me.model.api.ApiService
import com.lee.playcompose.service.AccountService
import com.lee.playcompose.service.helper.ModuleService

/**
 * 积分ViewModel
 * @author jv.lee
 * @date 2022/3/25
 */
class CoinViewModel : ViewModel() {

    private val api = createApi<ApiService>()
    val accountService: AccountService = ModuleService.find()

    private val pager by lazy {
        savedPager(
            savedKey = javaClass.simpleName.plus(accountService.getUserId()),
            initialKey = 1
        ) { api.getCoinRecordAsync(it).checkData() }
    }

    var viewStates by mutableStateOf(CoinViewState(savedPager = pager))
        private set
}

data class CoinViewState(
    val savedPager: SavedPager<CoinRecord>,
    val listState: LazyListState = LazyListState(),
    val detailsData: DetailsData = DetailsData(
        title = app.getString(R.string.coin_help_title),
        link = ApiConstants.URI_COIN_HELP
    )
)