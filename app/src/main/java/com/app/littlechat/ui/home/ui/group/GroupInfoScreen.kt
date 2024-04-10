package com.app.littlechat.ui.home.ui.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.getColors
import com.app.littlechat.utility.showToast

@Composable
fun ViewGroupScreen(navActions: HomeNavigationActions) {
    MainContent(navActions = navActions)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupInfoScreen(
    viewmodel: CreateGroupViewmodel = hiltViewModel(), navActions: HomeNavigationActions
) {

    val context = LocalContext.current

    val state = viewmodel.createGroupUiState.value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomToolbar(title = stringResource(id = R.string.group_info),
                onBackPress = {
                    navActions.popBack()
                })

            AsyncImage(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(200.dp)
                    .shadow(20.dp, shape = CircleShape, clip = true)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                model = if (viewmodel.image != null) viewmodel.image else Constants.DUMMY_URL,
                contentScale = ContentScale.Crop,
                contentDescription = viewmodel.name,
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 20.dp),
                text = viewmodel.name ?: "",
                fontSize = 20.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(offset = Offset(1f, 1f), blurRadius = 3.5f),
                    color = getColors().primary
                )
            )

            AnimatedVisibility(viewmodel.selectedMembers.isNotEmpty()) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)) {
                    itemsIndexed(viewmodel.selectedMembers, key = { _, user ->
                        user.id
                    }) { _, user ->
                        Box(
                            Modifier
                                .padding(10.dp)
                                .animateItemPlacement()) {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {},
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
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
                                    if (viewmodel.isMyGroup(user.id)) {
                                        Text(
                                            text = stringResource(id = R.string.admin),
                                            modifier = Modifier.background(
                                                Color(22, 200, 22, 120), shape = CircleShape
                                            ).padding(horizontal = 6.dp),
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
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
        viewmodel.getGroupData(true)
    }

}

@Preview(showBackground = true)
@Composable
private fun CreateGroupPrev() {
    MainContent(navActions = HomeNavigationActions(rememberNavController()))
}