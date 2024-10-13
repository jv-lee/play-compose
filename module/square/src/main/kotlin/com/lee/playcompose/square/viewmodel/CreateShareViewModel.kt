package com.lee.playcompose.square.viewmodel

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.base.extensions.lowestTime
import com.lee.playcompose.base.viewmodel.BaseMVIViewModel
import com.lee.playcompose.base.viewmodel.IViewEvent
import com.lee.playcompose.base.viewmodel.IViewIntent
import com.lee.playcompose.base.viewmodel.IViewState
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.square.model.api.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 创建分享内容viewModel
 * @author jv.lee
 * @date 2022/3/21
 */
class CreateShareViewModel : BaseMVIViewModel<CreateShareViewState, CreateShareViewEvent, CreateShareViewIntent>() {

    private val api = createApi<ApiService>()

    override fun initViewState() = CreateShareViewState()

    override fun dispatch(intent: CreateShareViewIntent) {
        when (intent) {
            is CreateShareViewIntent.ChangeShareTitle -> {
                changeShareTitle(intent.title)
            }
            is CreateShareViewIntent.ChangeShareContent -> {
                changeShareContent(intent.content)
            }
            is CreateShareViewIntent.RequestShare -> {
                requestShare()
            }
        }
    }

    private fun changeShareTitle(title: String) {
        if (title.length > 100) return
        _viewStates = _viewStates.copy(shareTitle = title)
    }

    private fun changeShareContent(content: String) {
        if (content.length > 100) return
        _viewStates = _viewStates.copy(shareContent = content)
    }

    private fun requestShare() {
        viewModelScope.launch {
            // 校验输入格式
            if (TextUtils.isEmpty(_viewStates.shareTitle) ||
                TextUtils.isEmpty(_viewStates.shareContent)
            ) {
                _viewEvents.send(CreateShareViewEvent.CreateFailed("title || content is empty."))
                return@launch
            }
            flow {
                val response = api.postShareDataSync(_viewStates.shareTitle, _viewStates.shareContent)
                emit(response.checkData())
            }.onStart {
                _viewStates = _viewStates.copy(isLoading = true)
            }.catch { error ->
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(CreateShareViewEvent.CreateFailed(error.message ?: ""))
            }.lowestTime().collect {
                _viewStates = _viewStates.copy(isLoading = false)
                _viewEvents.send(CreateShareViewEvent.CreateSuccess)
            }
        }
    }
}

data class CreateShareViewState(
    val shareTitle: String = "",
    val shareContent: String = "",
    val isLoading: Boolean = false
) : IViewState

sealed class CreateShareViewEvent : IViewEvent {
    object CreateSuccess : CreateShareViewEvent()
    data class CreateFailed(val message: String) : CreateShareViewEvent()
}

sealed class CreateShareViewIntent : IViewIntent {
    object RequestShare : CreateShareViewIntent()
    data class ChangeShareTitle(val title: String) : CreateShareViewIntent()
    data class ChangeShareContent(val content: String) : CreateShareViewIntent()
}