package com.lee.playcompose.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lee.playandroid.library.service.hepler.ModuleService
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.utils.ShareUtil
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.ui.widget.ActionMode
import com.lee.playcompose.service.MeService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2022/3/30
 * @description
 */
class DetailsViewModel(private val details: DetailsData) : ViewModel() {

    private val meService = ModuleService.find<MeService>()

    var viewStates by mutableStateOf(DetailsViewState())
        private set

    private val _viewEvents = Channel<DetailsViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    init {
        dispatch(DetailsViewAction.UpdateMoreButtonVisible)
    }

    fun dispatch(action: DetailsViewAction) {
        when (action) {
            is DetailsViewAction.UpdateMoreButtonVisible -> {
                updateMoreButtonVisible()
            }
            is DetailsViewAction.RequestCollectDetails -> {
                requestCollect()
            }
            is DetailsViewAction.ShareDetails -> {
                shareDetails()
            }
        }
    }

    private fun updateMoreButtonVisible() {
        val actionMode =
            if (details.id != DetailsData.EMPTY_ID) ActionMode.Menu else ActionMode.Default
        viewStates = viewStates.copy(actionModel = actionMode)
    }

    private fun requestCollect() {
        viewModelScope.launch {
            //已收藏直接返回结果
            if (details.isCollect) {
                _viewEvents.send(DetailsViewEvent.CollectEvent(app.getString(R.string.menu_collect_completed)))
                return@launch
            }

            flow {
                val response = meService.requestCollectAsync(details.id.toLong())
                if (response.errorCode == ApiConstants.REQUEST_OK) {
                    details.isCollect = true
                    emit(response.data)
                } else {
                    throw RuntimeException(response.errorMsg)
                }
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(DetailsViewEvent.CollectEvent(error.message ?: "未知错误"))
            }.collect {
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(DetailsViewEvent.CollectEvent(app.getString(R.string.menu_collect_complete)))
            }
        }
    }

    private fun shareDetails() {
        ShareUtil.shareText(app, "${details.title}:${details.link}")
    }

    class CreateFactory(private val details: DetailsData) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(DetailsData::class.java).newInstance(details)
        }
    }

}

data class DetailsViewState(
    val actionModel: ActionMode = ActionMode.Default,
    val isLoading: Boolean = false
)

sealed class DetailsViewEvent {
    data class CollectEvent(val message: String) : DetailsViewEvent()
}

sealed class DetailsViewAction {
    object UpdateMoreButtonVisible : DetailsViewAction()
    object RequestCollectDetails : DetailsViewAction()
    object ShareDetails : DetailsViewAction()
}

