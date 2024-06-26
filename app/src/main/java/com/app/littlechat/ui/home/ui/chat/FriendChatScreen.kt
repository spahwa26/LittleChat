package com.app.littlechat.ui.home.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.ProgressDialog
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.utility.showToast

@Composable
fun FriendChatScreen(viewmodel: ChatViewmodel = hiltViewModel(), navAction: HomeNavigationActions) {
    LaunchedEffect(Unit) {
        viewmodel.initChat()
    }
    val context = LocalContext.current
    val state = viewmodel.chatUiState.value
    Column(modifier = Modifier.fillMaxSize()) {
        CustomToolbar(
            title = viewmodel.name ?: "",
            onBackPress = { navAction.popBack() },
            rightButtonIcon = R.drawable.ic_menu,
            mutableState = viewmodel.popupMenuState,
            onRightBtnTap = {
                viewmodel.popupMenuState.value = true
            },
            content = {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.unfriend)) },
                    onClick = {
                        viewmodel.popupMenuState.value = false
                        viewmodel.warningText = R.string.remove_friend_warning
                        viewmodel.confirmationCallback = { viewmodel.removeFriend() }
                        viewmodel.warningAlert.value = true
                    }
                )
            }
        )
        ChatLayout(viewmodel = viewmodel)

        ProgressDialog(state = viewmodel.progressBarState)
    }


    WarningAlert(viewmodel = viewmodel)

    if (state is ChatViewmodel.ChatUiState.Error) {
        context.showToast(txt = state.msg)
        viewmodel.setIdle()
    }
    if (state is ChatViewmodel.ChatUiState.LocalMessage) {
        context.showToast(intRes = state.msg)
        viewmodel.setIdle()
    }
    if (state is ChatViewmodel.ChatUiState.RemovedOrLeftSuccess) {
        context.showToast(txt = stringResource(id = R.string.friend_removed, viewmodel.name ?: ""))
        viewmodel.setIdle()
        navAction.popBack()
    }

}


