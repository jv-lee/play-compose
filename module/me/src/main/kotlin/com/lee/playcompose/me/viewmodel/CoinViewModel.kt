package com.lee.playcompose.me.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.CoinRecord
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.extensions.localPager
import com.lee.playcompose.common.paging.extensions.pager
import com.lee.playcompose.me.R
import com.lee.playcompose.me.model.api.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * @author jv.lee
 * @date 2022/3/25
 * @description
 */
class CoinViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        localPager(initialKey = 1) { api.getCoinRecordAsync(it).checkData().data }
    }

    var viewStates by mutableStateOf(CoinViewState(pagingData = pager))
        private set
}

data class CoinViewState(
    val pagingData: Flow<PagingData<CoinRecord>>,
    val listState: LazyListState = LazyListState(),
    val detailsData: DetailsData = DetailsData(
        title = app.getString(R.string.coin_help_title),
        link = ApiConstants.URI_COIN_HELP
    )
)