package com.app.littlechat.ui.home.ui.findfriends

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.ProfileImage
import com.app.littlechat.ui.commoncomposables.SingleLineText
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun FindFriendsScreen(
    viewmodel: FindFriendsViewmodel = hiltViewModel(),
    navActions: HomeNavigationActions
) {
    val state = viewmodel.friendsUiState.value
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxSize()
    ) {
        CustomToolbar(
            title = stringResource(id = R.string.find_friends),
            onBackPress = {
                navActions.popBack()
            })


        TextField(
            value = viewmodel.searchText.value,
            onValueChange = { viewmodel.updateText(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .focusRequester(focusRequester),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
            placeholder = { Text(text = "Search") }
        )


//        OutlinedTextField(
//            value = viewmodel.searchText.value,
//            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
//            onValueChange = {
//                viewmodel.updateText(it)
//            })

        if (state is FindFriendsViewmodel.FriendsUiState.Success) {
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
                                    focusRequester.freeFocus()
                                    scope.launch {
                                        delay(1000)
                                        navActions.navigateToProfile(user.id)
                                        this.cancel()
                                    }
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
                                    SingleLineText(text = user.name)
                                    SingleLineText(text = user.email)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}