package com.app.littlechat.ui.home.ui.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.home.navigation.HomeNavigationActions

@Composable
fun CreateGroupScreen(navActions: HomeNavigationActions) {
    MainContent(navActions = navActions)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(
    viewmodel: CreateGroupViewmodel = hiltViewModel(),
    navActions: HomeNavigationActions,
    groupId: String? = null
) {
    Box(modifier = Modifier.fillMaxSize())
    {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize()
        ) {
            CustomToolbar(
                title = stringResource(id = if (groupId.isNullOrBlank()) R.string.create_group else R.string.edit_group),
                onBackPress = {
                    navActions.popBack()
                }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileImage(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(60.dp)
                        .clickable { viewmodel.selectedMembers.shuffle() },
                    imageUrl = R.drawable.ic_upload_placeholder,
                    name = stringResource(
                        id = R.string.upload_image
                    )
                )
                TextField(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .weight(1f),
                    value = viewmodel.groupName.value,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.group_name),
                            modifier = Modifier.background(
                                Color.Transparent
                            )
                        )
                    },
                    onValueChange = {
                        viewmodel.groupName.value = it
                    },
                )
            }


            AnimatedVisibility(viewmodel.selectedMembers.isNotEmpty()) {
                LazyRow(modifier = Modifier.height(60.dp)) {
                    items(viewmodel.selectedMembers, key = {
                        it.id
                    }) {
                        Box(modifier = Modifier.animateItemPlacement()) {
                            AsyncImage(
                                model = it.image,
                                contentDescription = it.name,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .size(60.dp)
                                    .clip(CircleShape),
                                placeholder = painterResource(id = R.drawable.ic_person),
                                error = painterResource(id = R.drawable.ic_person),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            )

                            Image(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset((-10).dp)
                                    .size(20.dp)
                                    .background(
                                        Color.White, CircleShape
                                    ),
                                painter = painterResource(id = R.drawable.ic_cross),
                                contentDescription = stringResource(
                                    id = R.string.cancel
                                )
                            )

                        }
                    }
                }
            }

            if (viewmodel.usersList.isNotEmpty()) {
                LazyColumn {
                    itemsIndexed(viewmodel.usersList, key = { _, user ->
                        user.id
                    }) { index, user ->
                        Box(Modifier.padding(10.dp)) {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        user.isAdded = !user.isAdded
                                        viewmodel.updateItem(user, index)
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Row {
                                        ProfileImage(
                                            modifier = Modifier
                                                .size(50.dp), user.image, user.name
                                        )
                                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                            Text(text = user.name)
                                            Text(text = user.email)
                                        }
                                    }

                                    Checkbox(checked = user.isAdded, onCheckedChange = {})
                                }
                            }
                        }
                    }
                }
            }

        }

        if (viewmodel.selectedMembers.isNotEmpty()) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = { }) {
                Image(
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = stringResource(
                        id = R.string.create_group
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateGroupPrev() {
    MainContent(navActions = HomeNavigationActions(rememberNavController()))
}