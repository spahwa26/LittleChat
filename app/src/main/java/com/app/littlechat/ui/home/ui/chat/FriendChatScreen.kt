package com.app.littlechat.ui.home.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.utility.showToast

@Composable
fun FriendChatScreen(viewmodel: ChatViewmodel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewmodel.initChat()
    }
    val context = LocalContext.current
    val state = viewmodel.chatUiState.value
    if (state is ChatViewmodel.ChatUiState.Error) {
        context.showToast(txt = state.msg)
    }
    ChatLayout(viewmodel = viewmodel)
}


