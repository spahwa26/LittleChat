package com.app.littlechat.ui.home.ui.chat

import android.support.annotation.StringRes
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
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.ChatRepository
import com.app.littlechat.ui.home.navigation.HomeArgs
import com.app.littlechat.utility.Constants.Companion.DUMMY_URL
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
    val chatId: String? = savedStateHandle[HomeArgs.CHAT_ID_ARG]
    private val friendChatId: String by lazy {
        if (!chatId.isNullOrBlank() && userPreferences.id != null) {
            if (chatId > userPreferences.id!!)
                chatId + "__" + userPreferences.id
            else
                userPreferences.id + "__" + chatId
        } else ""
    }
    val name: String? = savedStateHandle[HomeArgs.NAME_ARG]
    val friendImage: String? = savedStateHandle[HomeArgs.IMAGE_ARG]
    private var participant = listOf<User>()
    private var isGroupChat = false


    private val _chatUiState: MutableState<ChatUiState?> = mutableStateOf(null)
    val chatUiState: State<ChatUiState?> = _chatUiState
    val message = mutableStateOf("")
    val chatList = mutableStateListOf<Chat>()
    val progressBarState = mutableStateOf(false)
    val popupMenuState = mutableStateOf(false)


    fun initChat(isGroup: Boolean = false) {
        isGroupChat = isGroup
        repository.isGroupChat = isGroup
        if (isGroupChat) {
            chatId?.let { chatId ->
                repository.getParticipantsData(chatId) {
                    when (it) {
                        is CustomResult.Success -> {
                            participant = it.data
                            setChatListener()
                        }

                        is CustomResult.Error -> {

                        }
                    }
                }
            }
        } else
            setChatListener()
    }

    private fun setChatListener() {
        repository.setChatListener(if (isGroupChat) chatId ?: "" else friendChatId) {
            if (it is CustomResult.Success) {
                it.data?.let { chat ->
                    chatList.add(chat)
                }
            }
        }
    }

    fun isMyGroup() = (chatId?.contains(userPreferences.id ?: DUMMY_URL) == true)

    fun leaveGroup() {
        if (chatId == null) {
            _chatUiState.value = ChatUiState.LocalMessage(R.string.something_wrong)
            return
        }
        progressBarState.value = true
        repository.leaveGroup(chatId) {
            progressBarState.value = false
            when (it) {
                is CustomResult.Success -> {
                    _chatUiState.value = ChatUiState.RemovedOrLeftSuccess
                }

                is CustomResult.Error -> {
                    _chatUiState.value = ChatUiState.Error(it.exception.message)
                }
            }
        }
    }

    fun removeFriend() {
        if (chatId == null) {
            _chatUiState.value = ChatUiState.LocalMessage(R.string.something_wrong)
            return
        }

        progressBarState.value = true
        repository.removeFriend(chatId) {
            progressBarState.value = false
            when (it) {
                is CustomResult.Success -> {
                    _chatUiState.value = ChatUiState.RemovedOrLeftSuccess
                }

                is CustomResult.Error -> {
                    _chatUiState.value = ChatUiState.Error(it.exception.message)
                }
            }
        }
    }

    fun sendMessage() {
        val context = userPreferences.context
        if (message.value.trim().isBlank()) {
            _chatUiState.value = ChatUiState.Error(context.getString(R.string.please_type))
            return
        }
        if (!context.isNetworkConnected()) {
            _chatUiState.value = ChatUiState.Error(context.getString(R.string.connect_to_internet))
            return
        }
        val myId = userPreferences.id ?: ""
        val chat = Chat(
            myId,
            chatId ?: "",
            userPreferences.image ?: "",
            userPreferences.name ?: "",
            message.value,
            System.currentTimeMillis(), "sent"
        )
        message.value = ""
        _chatUiState.value = ChatUiState.Loading
        viewModelScope.launch {
            repository.sendMessage(chat, if (isGroupChat) chatId ?: "" else friendChatId) {
                if (it is CustomResult.Error)
                    _chatUiState.value = ChatUiState.Error(it.exception.message)
            }
        }
    }

    fun setIdle() {
        _chatUiState.value = null
    }

    fun getUserImage(id: String): String {
        var image = ""
        if (isGroupChat)
            for (user in participant) {
                if (user.id == id)
                    image = user.image
            }
        else image = friendImage ?: ""
        return image
    }

    fun getMyImage() = userPreferences.image ?: ""

    sealed class ChatUiState {
        data object Loading : ChatUiState()
        data object RemovedOrLeftSuccess : ChatUiState()
        data class Error(val msg: String?) : ChatUiState()
        data class LocalMessage(@StringRes val msg: Int) : ChatUiState()
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListeners()
    }

    fun getId() = userPreferences.id ?: ""

}