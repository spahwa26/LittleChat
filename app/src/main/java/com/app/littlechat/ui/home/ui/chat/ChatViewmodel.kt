package com.app.littlechat.ui.home.ui.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.network.ChatRepository
import com.app.littlechat.ui.home.navigation.HomeArgs
import com.app.littlechat.utility.isNetworkConnected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewmodel @Inject constructor(
    private val repository: ChatRepository,
    savedStateHandle: SavedStateHandle,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val id: String? = savedStateHandle[HomeArgs.CHAT_ID_ARG]
    private val chatId: String by lazy {
        if (!id.isNullOrBlank() && userPreferences.id != null) {
            if (id > userPreferences.id!!)
                id + "__" + userPreferences.id
            else
                userPreferences.id + "__" + id
        } else ""
    }
    val name: String? = savedStateHandle[HomeArgs.NAME_ARG]
    val image: String? = savedStateHandle[HomeArgs.IMAGE_ARG]
    private val _chatUiState: MutableState<ChatUiState?> = mutableStateOf(null)
    val chatUiState: State<ChatUiState?> = _chatUiState
    val message = mutableStateOf("")
    val chatList = mutableStateListOf<Chat>()

    init {
        setChatListener()
    }

    private fun setChatListener() {
        viewModelScope.launch {
            repository.setChatListener(chatId) {
                if (it is CustomResult.Success) {
                    it.data?.let { chat ->
                        chatList.add(chat)
                    }
                }
            }
        }
    }

    fun sendMessage() {
        val context = userPreferences.context
        if (message.value.trim().isBlank()) {
            updateChatState(ChatUiState.Error(context.getString(R.string.please_type)))
            return
        }
        if (!context.isNetworkConnected()) {
            updateChatState(ChatUiState.Error(context.getString(R.string.connect_to_internet)))
            return
        }
        val myId = userPreferences.id ?: ""
        val chat = Chat(
            myId,
            id ?: "",
            userPreferences.image ?: "",
            userPreferences.name ?: "",
            message.value,
            System.currentTimeMillis(), "sent"
        )
        message.value = ""
        _chatUiState.value = ChatUiState.Loading
        viewModelScope.launch {
            repository.sendMessage(chat, chatId) {
                if (it is CustomResult.Error)
                    updateChatState(ChatUiState.Error(it.exception.message))
            }
        }
    }

    private fun updateChatState(state: ChatUiState) {
        _chatUiState.value = state
        _chatUiState.value = ChatUiState.Idle
    }

    sealed class ChatUiState {
        data object Idle : ChatUiState()
        data object Loading : ChatUiState()
        data object Success : ChatUiState()
        data class Error(val msg: String?) : ChatUiState()
    }

}