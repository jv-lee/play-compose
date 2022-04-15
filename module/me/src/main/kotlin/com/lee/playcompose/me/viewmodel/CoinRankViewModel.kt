package com.lee.playcompose.me.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lee.playcompose.base.core.ApplicationExtensions
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.*
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.common.paging.extensions.pager
import com.lee.playcompose.me.R
import com.lee.playcompose.me.model.api.ApiService
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * @author jv.lee
 * @date 2022/3/28
 * @description
 */
class CoinRankViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    private val pager by lazy {
        pager(initialKey = 1) {
            api.getCoinRankAsync(it).checkData().swapRankList(it)
        }
    }

    var viewStates by mutableStateOf(CoinRankViewState(pagingData = pager))
        private set

    private fun PageData<CoinRank>.swapRankList(page: Int): PageData<CoinRank> {
        if (page == 1) {
            val data = data.toMutableList()

            val list = arrayListOf<CoinRank>()
            for (index in 0..2) {
                if (data.size > 0) {
                    list.add(data.removeAt(0))
                }
            }

            if (list.size >= 2) {
                //排行榜UI显示 0 —><- 1 位置数据对掉
                Collections.swap(list, 0, 1)
            }

            viewStates = viewStates.copy(topList = list)
        }
        return this
    }
}

data class CoinRankViewState(
    val topList: List<CoinRank> = emptyList(),
    val pagingData: Flow<PagingData<CoinRank>>,
    val listState: LazyListState = LazyListState(),
    val detailsData: DetailsData = DetailsData(
        title = ApplicationExtensions.app.getString(R.string.coin_help_title),
        link = ApiConstants.URI_COIN_HELP
    )
)