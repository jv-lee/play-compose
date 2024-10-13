package com.lee.playcompose.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.extensions.lowestTime
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.constants.ApiConstants
import com.lee.playcompose.common.entity.DetailsData
import com.lee.playcompose.common.ui.widget.header.ActionMode
import com.lee.playcompose.details.R
import com.lee.playcompose.service.MeService
import com.lee.playcompose.service.helper.ModuleService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 文章详情页viewModel
 * @author jv.lee
 * @date 2022/3/30
 */
class DetailsViewModel(private val details: DetailsData) :
    BaseMVIViewModel<DetailsViewState, DetailsViewEvent, DetailsViewIntent>() {

    private val meService = ModuleService.find<MeService>()

    init {
        moreButtonVisible()
    }

    override fun initViewState() = DetailsViewState()

    override fun dispatch(intent: DetailsViewIntent) {
        when (intent) {
            is DetailsViewIntent.RequestCollectDetails -> {
                requestCollect()
            }

            is DetailsViewIntent.ShareDetails -> {
                shareDetails()
            }
        }
    }

    private fun moreButtonVisible() {
        val actionMode =
            if (details.id != DetailsData.EMPTY_ID) ActionMode.Menu else ActionMode.Default
        _viewStates = _viewStates.copy(actionModel = actionMode)
    }

    private fun requestCollect() {
        viewModelScope.launch {
            // 已收藏直接返回结果
            if (details.isCollect) {
                _viewEvents.send(
                    DetailsViewEvent.CollectEvent(app.getString(R.string.menu_collect_completed))
                )
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
                _viewStates = _viewStates.copy(isLoading = true)
            }.catch { error ->
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(DetailsViewEvent.CollectEvent(error.message))
            }.lowestTime().collect {
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(
                    DetailsViewEvent.CollectEvent(app.getString(R.string.menu_collect_complete))
                )
            }
        }
    }

    private fun shareDetails() {
        viewModelScope.launch {
            _viewEvents.send(DetailsViewEvent.ShareEvent("${details.title}:${details.link}"))
        }
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
) : IViewState

sealed class DetailsViewEvent : IViewEvent {
    data class CollectEvent(val message: String?) : DetailsViewEvent()
    data class ShareEvent(val shareText: String) : DetailsViewEvent()
}

sealed class DetailsViewIntent : IViewIntent {
    object RequestCollectDetails : DetailsViewIntent()
    object ShareDetails : DetailsViewIntent()
}
