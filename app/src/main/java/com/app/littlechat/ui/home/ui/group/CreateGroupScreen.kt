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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.PermissionComposable
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.commoncomposables.ProgressDialog
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.utility.Constants.Companion.NULL
import com.app.littlechat.utility.getResizedBitmap
import com.app.littlechat.utility.showToast

@Composable
fun CreateGroupScreen(navActions: HomeNavigationActions) {
    MainContent(navActions = navActions)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(
    viewmodel: CreateGroupViewmodel = hiltViewModel(), navActions: HomeNavigationActions
) {

    val context = LocalContext.current

    val state = viewmodel.createGroupUiState.value

    val triggerPermissionComposable = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize()
        ) {
            CustomToolbar(title = stringResource(id = if (viewmodel.grpId.equals(NULL)) R.string.create_group else R.string.update_group),
                onBackPress = {
                    navActions.popBack()
                })

            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileImage(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(60.dp)
                        .clickable { triggerPermissionComposable.value = true },
                    imageUrl = if (viewmodel.imageUri.value != null) ImageRequest.Builder(
                        LocalContext.current
                    ).data(viewmodel.imageUri.value).build()
                    else if (!viewmodel.image.isNullOrBlank()) viewmodel.image ?: ""
                    else R.drawable.ic_upload_placeholder,
                    name = stringResource(
                        id = R.string.upload_image
                    )
                )
                TextField(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .weight(1f),
                    value = viewmodel.groupName.value, //todo: fix rerendering of all components when changing TextField value(in every screen)
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .height(60.dp)
                            .fillMaxWidth()
                    ) {
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

                    HorizontalDivider()
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
                                            modifier = Modifier.size(50.dp), user.image, user.name
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
            FloatingActionButton(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
                onClick = { viewmodel.createGroup() }) {
                Image(
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = stringResource(
                        id = R.string.create_group
                    )
                )
            }
        }
    }

    PermissionComposable(state = triggerPermissionComposable) {
        viewmodel.imageUri.value = context.getResizedBitmap(
            uri = it, maxSize = 600, fileName = viewmodel.getImageName()
        )
    }

    ProgressDialog(state = viewmodel.progressState)

    if (state is CreateGroupViewmodel.CreateGroupUiState.Success) {
        if (state.isEdit) {
            context.showToast(R.string.group_updated)
        } else {
            context.showToast(R.string.group_created)
            navActions.popBack()
        }
        viewmodel.setIdle()
    }

    if (state is CreateGroupViewmodel.CreateGroupUiState.LocalMessage) {
        context.showToast(state.msg)
        viewmodel.setIdle()
    }

    if (state is CreateGroupViewmodel.CreateGroupUiState.Error) {
        context.showToast(txt = state.e)
        viewmodel.setIdle()
    }



    LaunchedEffect(Unit) {
        viewmodel.getGroupData()
    }

}

@Preview(showBackground = true)
@Composable
private fun CreateGroupPrev() {
    MainContent(navActions = HomeNavigationActions(rememberNavController()))
}