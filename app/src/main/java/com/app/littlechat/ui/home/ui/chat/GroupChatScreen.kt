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
import com.app.littlechat.utility.getEncodedUrl
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
    Column(modifier = Modifier.fillMaxSize()) {
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
                        text = { Text(text = stringResource(id = R.string.group_info)) },
                        onClick = {
                            viewmodel.popupMenuState.value = false
                        }
                    )
                    if (!viewmodel.isMyGroup())
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.leave_Group)) },
                            onClick = {
                                viewmodel.popupMenuState.value = false
                                viewmodel.leaveGroup()
                            }
                        )
                    if (viewmodel.isMyGroup()) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.update_group)) },
                            onClick = {
                                viewmodel.popupMenuState.value = false
                                navAction.navigateToCreateGroup(
                                    viewmodel.chatId,
                                    viewmodel.name,
                                    viewmodel.friendImage.getEncodedUrl()
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete_group)) },
                            onClick = {
                                viewmodel.popupMenuState.value = false
                                navAction.navigateToCreateGroup(
                                    viewmodel.chatId,
                                    viewmodel.name,
                                    viewmodel.friendImage.getEncodedUrl()
                                )
                            }
                        )
                    }
                }
            )

            ChatLayout(viewmodel = viewmodel)
        }


        ProgressDialog(state = viewmodel.progressBarState)
    }


    if (state is ChatViewmodel.ChatUiState.Error) {
        context.showToast(txt = state.msg)
        viewmodel.setIdle()
    }
    if (state is ChatViewmodel.ChatUiState.LocalMessage) {
        context.showToast(intRes = state.msg)
        viewmodel.setIdle()
    }
    if (state is ChatViewmodel.ChatUiState.RemovedOrLeftSuccess) {
        context.showToast(txt = stringResource(id = R.string.group_left_success))
        viewmodel.setIdle()
        navAction.popBack()
    }
}


