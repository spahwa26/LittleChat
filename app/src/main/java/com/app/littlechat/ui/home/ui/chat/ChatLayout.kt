package com.app.littlechat.ui.home.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.ChatText
import com.app.littlechat.ui.commoncomposables.ProfileImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatLayout(viewmodel: ChatViewmodel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(reverseLayout = true, modifier = Modifier.weight(1f)) {
            items(viewmodel.chatList.reversed()) { chat ->

                val isMyMsg = chat.sender_id == viewmodel.getId()
                Box(Modifier.padding(10.dp).animateItemPlacement()) {

                    Row(
                        verticalAlignment = Alignment.Bottom,
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
                                imageUrl = viewmodel.getMyImage(),
                                name = chat.sender_name
                            )
                        } else {
                            ProfileImage(
                                modifier = Modifier.size(30.dp),
                                viewmodel.getUserImage(chat.sender_id),
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(15.dp),
                value = viewmodel.message.value,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                onValueChange = {
                    viewmodel.message.value = it
                },
                textStyle = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Image(
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 10.dp)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .padding(5.dp)
                    .clickable {
                        viewmodel.sendMessage()
                    },
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "send",
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
            )
        }
    }
}