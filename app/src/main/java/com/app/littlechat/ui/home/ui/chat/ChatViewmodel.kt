package com.app.littlechat.ui.home.ui.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.network.ChatRepository
import com.app.littlechat.ui.home.navigation.HomeArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewmodel @Inject constructor(
    private val repository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatId: String? = savedStateHandle[HomeArgs.CHAT_ID_ARG]
    val name: String? = savedStateHandle[HomeArgs.NAME_ARG]
    val image: String? = savedStateHandle[HomeArgs.IMAGE_ARG]
    private val _chatUiState: MutableState<ChatUiState?> = mutableStateOf(null)
    val chatUiState: State<ChatUiState?> = _chatUiState

    init {
        chatId?.let {
            getChatList(it)
        }
    }

    private fun getChatList(id: String) {
        _chatUiState.value = ChatUiState.Loading
        viewModelScope.launch {
            repository.getChats(id) {
                when (it) {
                    is CustomResult.Success -> {
                        _chatUiState.value = ChatUiState.Success(it.data)
                    }

                    is CustomResult.Error -> {
                        _chatUiState.value = ChatUiState.Error(it.exception.message)
                    }

                }
            }
        }
    }

    sealed class ChatUiState {
        data object Loading : ChatUiState()
        data class Success(val chatList: List<Chat>) : ChatUiState()
        data class Error(val e: String?) : ChatUiState()
    }

}