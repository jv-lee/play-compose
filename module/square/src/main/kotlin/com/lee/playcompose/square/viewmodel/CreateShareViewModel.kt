package com.lee.playcompose.square.viewmodel

import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
class CreateShareViewModel : BaseMVIViewModel<CreateShareViewEvent, CreateShareViewIntent>() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(CreateShareViewState())
        private set

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
        viewStates = viewStates.copy(shareTitle = title)
    }

    private fun changeShareContent(content: String) {
        if (content.length > 100) return
        viewStates = viewStates.copy(shareContent = content)
    }

    private fun requestShare() {
        viewModelScope.launch {
            // 校验输入格式
            if (TextUtils.isEmpty(viewStates.shareTitle) ||
                TextUtils.isEmpty(viewStates.shareContent)
            ) {
                _viewEvents.send(CreateShareViewEvent.CreateFailed("title || content is empty."))
                return@launch
            }
            flow {
                val response = api.postShareDataSync(viewStates.shareTitle, viewStates.shareContent)
                emit(response.checkData())
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(CreateShareViewEvent.CreateFailed(error.message ?: ""))
            }.lowestTime().collect {
                viewStates = viewStates.copy(isLoading = false)
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