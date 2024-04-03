package com.app.littlechat.ui.home.ui.firends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.ui.home.ui.HomeViewmodel
import com.app.littlechat.utility.Constants
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun FriendsScreen(
    viewmodel: HomeViewmodel,
    navActions: HomeNavigationActions,
    bottomPadding: Dp
) {
    val state = viewmodel.friendsUiState.value
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding)
    ) {
        if (state is HomeViewmodel.FriendsUiState.Success) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.friendList) { user ->
                    Box(Modifier.padding(10.dp)) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val encodedUrl = URLEncoder.encode(
                                        user.image.ifBlank { Constants.DUMMY_URL },
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navActions.navigateToChat(user.id, user.name, encodedUrl)

                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                ProfileImage(modifier = Modifier.size(50.dp), user.image, user.name)
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