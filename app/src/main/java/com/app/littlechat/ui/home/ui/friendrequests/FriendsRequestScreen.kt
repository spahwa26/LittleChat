package com.app.littlechat.ui.home.ui.friendrequests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.NoDataView
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.commoncomposables.PullToRefreshLazyColumn
import com.app.littlechat.ui.commoncomposables.SingleLineText
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.ui.theme.RedCustom
import com.app.littlechat.utility.Constants.Companion.SENT


@Composable
fun FriendsRequestScreen(
    requestViewmodel: RequestViewmodel = hiltViewModel(), navActions: HomeNavigationActions
) {
    val state = requestViewmodel.reqUiState.value
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        CustomToolbar(
            title = stringResource(id = R.string.friend_requests),
            onBackPress = { navActions.popBack() })
        PullToRefreshLazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            items = requestViewmodel.userData,
            isRefreshing = requestViewmodel.isRefreshing.collectAsState().value,
            onRefresh = {
                requestViewmodel.getRequestList(true)
            }) { user ->
            Column(Modifier.padding(10.dp)) {
                Card(
                    shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navActions.navigateToProfile(user.id)

                        }, elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        ProfileImage(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    navActions.navigateToProfile(user.id)
                                }, user.image, user.name
                        )
                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                            SingleLineText(text = user.name)
                            SingleLineText(text = user.email)
                        }
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)
                            .padding(horizontal = 10.dp)
                            .defaultMinSize(minHeight = 45.dp)
                    ) {
                        if (state is RequestViewmodel.RequestUiState.ActionLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(25.dp))
                        } else {
                            if (user.status != SENT) {
                                Button(
                                    modifier = Modifier.weight(1f, true),
                                    onClick = {
                                        requestViewmodel.acceptRequest(user)
                                    }) {
                                    Text(
                                        text = stringResource(R.string.accept_request)
                                    )
                                }
                                Spacer(
                                    modifier = Modifier.width(
                                        10.dp
                                    )
                                )
                            }
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = RedCustom),
                                modifier = Modifier.weight(1f, true),
                                onClick = {
                                    requestViewmodel.cancelRequest(user.id)
                                }) {
                                Text(
                                    text = stringResource(id = if (user.status == SENT) R.string.cancel_request else R.string.reject_request),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    if (state is RequestViewmodel.RequestUiState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }


    if (state is RequestViewmodel.RequestUiState.NoData && requestViewmodel.userData.isEmpty()) {
        NoDataView(text = stringResource(id = R.string.no_requests))
    }
}