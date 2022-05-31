package com.lee.playcompose.square.viewmodel

import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lee.playcompose.common.extensions.checkData
import com.lee.playcompose.common.extensions.createApi
import com.lee.playcompose.square.model.api.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 * @author jv.lee
 * @date 2022/3/21
 */
class CreateShareViewModel : ViewModel() {

    private val api = createApi<ApiService>()

    var viewStates by mutableStateOf(CreateShareViewState())
        private set

    private val _viewEvents = Channel<CreateShareViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: CreateShareViewAction) {
        when (action) {
            is CreateShareViewAction.ChangeShareTitle -> {
                changeShareTitle(action.title)
            }
            is CreateShareViewAction.ChangeShareContent -> {
                changeShareContent(action.content)
            }
            is CreateShareViewAction.RequestShare -> {
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
            flow {
                delay(500)
                // 校验输入格式
                if (TextUtils.isEmpty(viewStates.shareTitle) || TextUtils.isEmpty(viewStates.shareContent)) {
                    throw IllegalArgumentException("title || content is empty.")
                }

                val response = api.postShareDataSync(viewStates.shareTitle, viewStates.shareContent)
                emit(response.checkData())
            }.onStart {
                viewStates = viewStates.copy(isLoading = true)
            }.catch { error ->
                viewStates = viewStates.copy(isLoading = false)
                _viewEvents.send(CreateShareViewEvent.CreateFailed(error.message ?: ""))
            }.collect {
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
)

sealed class CreateShareViewEvent {
    object CreateSuccess : CreateShareViewEvent()
    data class CreateFailed(val message: String) : CreateShareViewEvent()
}

sealed class CreateShareViewAction {
    object RequestShare : CreateShareViewAction()
    data class ChangeShareTitle(val title: String) : CreateShareViewAction()
    data class ChangeShareContent(val content: String) : CreateShareViewAction()
}