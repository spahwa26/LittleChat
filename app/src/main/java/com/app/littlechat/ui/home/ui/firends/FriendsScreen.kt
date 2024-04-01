package com.app.littlechat.ui.home.ui.firends

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.app.littlechat.ui.home.ui.HomeViewmodel


@Composable
fun FriendsScreen(viewmodel: HomeViewmodel) {
    val state = viewmodel.friendsUiState.value
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (state is HomeViewmodel.FriendsUiState.Success) {
            LazyColumn {
                items(state.friendList) { user ->
                    Box(Modifier.padding(10.dp)) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(user.image)
                                        .size(
                                            Size(
                                                100,
                                                100
                                            )
                                        ) // Set the target size to load the image at.
                                        .build()
                                )
                                Image(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        ),
                                    contentScale = ContentScale.Crop,
                                    painter = painter,
                                    contentDescription = user.name,
                                )
                                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                    Text(text = user.name)
                                    Text(text = user.email)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}