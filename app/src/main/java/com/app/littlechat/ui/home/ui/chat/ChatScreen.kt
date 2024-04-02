package com.app.littlechat.ui.home.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.ui.commoncomposables.ChatText
import com.app.littlechat.ui.commoncomposables.ProfileImage

@Composable
fun ChatScreen(uid: String?, myImage: String?, viewmodel: ChatViewmodel = hiltViewModel()) {
    val state = viewmodel.chatUiState.value
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        if (state is ChatViewmodel.ChatUiState.Success) {
            LazyColumn(reverseLayout = true) {
                items(state.chatList.reversed()) { chat ->

                    val isMyMsg = chat.sender_id == uid
                    Box(Modifier.padding(10.dp)) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isMyMsg) Arrangement.End else Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            if (isMyMsg) {
                                ChatText(
                                    msg = chat.message,
                                    modifier = Modifier
                                        .padding(start = 80.dp)
                                        .weight(1f, fill = false),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                ProfileImage(
                                    modifier = Modifier
                                        .size(30.dp),
                                    imageUrl = myImage ?: "",
                                    name = chat.sender_name
                                )
                            } else {
                                ProfileImage(
                                    modifier = Modifier.size(30.dp),
                                    viewmodel.image ?: "",
                                    chat.sender_name
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                ChatText(
                                    msg = chat.message,
                                    modifier = Modifier.padding(end = 80.dp),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                        }

                    }
                }
            }
        }
    }
}