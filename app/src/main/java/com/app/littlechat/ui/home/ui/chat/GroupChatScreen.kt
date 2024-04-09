package com.app.littlechat.ui.home.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.utility.showToast

@Composable
fun GroupChatScreen(viewmodel: ChatViewmodel = hiltViewModel(), navAction: HomeNavigationActions) {
    LaunchedEffect(Unit) {
        viewmodel.initChat(true)
    }
    val context = LocalContext.current
    val state = viewmodel.chatUiState.value
    if (state is ChatViewmodel.ChatUiState.Error) {
        context.showToast(txt = state.msg)
    }
    Column(modifier = Modifier.fillMaxSize()){
        CustomToolbar(title = viewmodel.name ?: "", onBackPress = {navAction.popBack()})
        ChatLayout(viewmodel = viewmodel)
    }
}


