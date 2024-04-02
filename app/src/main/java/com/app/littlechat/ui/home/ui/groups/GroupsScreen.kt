package com.app.littlechat.ui.home.ui.groups

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
import com.app.littlechat.ui.home.ui.HomeViewmodel


@Composable
fun GroupsScreen(viewmodel: HomeViewmodel, bottomPadding:Dp) {
    val state = viewmodel.groupsUiState.value
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)) {
        if (state is HomeViewmodel.GroupsUiState.Success) {
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
                                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                ProfileImage(modifier = Modifier.size(50.dp), user.image, user.name)
                                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                    Text(text = user.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}