package com.app.littlechat.ui.home.ui.firends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.NoDataView
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.commoncomposables.PullToRefreshLazyColumn
import com.app.littlechat.ui.commoncomposables.SingleLineText
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.ui.home.ui.HomeViewmodel
import com.app.littlechat.utility.getEncodedUrl


@Composable
fun FriendsScreen(
    viewmodel: HomeViewmodel,
    navActions: HomeNavigationActions,
    bottomPadding: Dp,
    name: String
) {
    val state = viewmodel.friendsUiState.value

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding)
    ) {
        CustomToolbar(title = stringResource(id = R.string.app_name))

        PullToRefreshLazyColumn(modifier = Modifier
            .fillMaxSize(),
            items = viewmodel.userData,
            isRefreshing = viewmodel.isRefreshing.value,
            onRefresh = {
                viewmodel.getFriendList(true)
            })
        { user ->
            Box(Modifier.padding(10.dp)) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            navActions.navigateToChat(
                                user.id,
                                user.name,
                                user.image.getEncodedUrl()
                            )

                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                }
            }
        }
    }



    if (state is HomeViewmodel.FriendsUiState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }


    if (state is HomeViewmodel.FriendsUiState.NoData) {
        NoDataView(text = stringResource(id = R.string.no_friends, name))
    }
}