package com.app.littlechat.ui.home.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.utility.Constants.Companion.DUMMY_URL
import com.app.littlechat.utility.showToast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ProfileScreen(
    profileViewmodel: ProfileViewmodel = hiltViewModel(), navActions: HomeNavigationActions
) {
    ProfileContent(profileViewmodel = profileViewmodel, navActions = navActions)
}


@Composable
fun ProfileContent(profileViewmodel: ProfileViewmodel, navActions: HomeNavigationActions) {
    val state = profileViewmodel.profileUiState.value
    val userData = profileViewmodel.userData.value
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        CustomToolbar(title = stringResource(id = R.string.profile), onBackPress = {
            navActions.popBack()
        })
        if (profileViewmodel.userData.value != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
            ) {


                AsyncImage(
                    modifier = Modifier
                        .size(240.dp)
                        .shadow(20.dp, shape = CircleShape, clip = true)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent),
                    model = if (userData?.image.isNullOrBlank()) DUMMY_URL else userData?.image,
                    contentScale = ContentScale.Crop,
                    contentDescription = "",

                    )


                OutlinedTextField(
                    modifier = Modifier
                        .padding(bottom = 15.dp, top = 30.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = userData?.name ?: "",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next
                    ),
                    label = { Text(text = stringResource(id = R.string.name)) },
                    onValueChange = {},
                    enabled = profileViewmodel.isMyProfile(),
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = color.primary,
                        disabledPlaceholderColor = color.primary,
                        disabledLabelColor = color.primary,
                        disabledContainerColor = Color.Transparent
                    )
                )


                OutlinedTextField(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = userData?.email ?: "",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                    ),
                    enabled = false,
                    label = { Text(text = stringResource(id = R.string.email)) },
                    onValueChange = {},
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = color.primary,
                        disabledPlaceholderColor = color.primary,
                        disabledLabelColor = color.primary,
                        disabledContainerColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = userData?.phone_number ?: "",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                    ),
                    label = { Text(text = stringResource(id = R.string.phone_number)) },
                    onValueChange = {},
                    enabled = profileViewmodel.isMyProfile(),
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = color.primary,
                        disabledPlaceholderColor = color.primary,
                        disabledLabelColor = color.primary,
                        disabledContainerColor = Color.Transparent
                    )
                )

            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 50.dp, top = 30.dp)
                .height(45.dp)
        ) {
            if (profileViewmodel.btnText.value == null || state == ProfileViewmodel.ProfileUiState.Loading) CircularProgressIndicator()
            else Row {
                Button(modifier = Modifier.weight(1f, false), onClick = {
                    profileViewmodel.handleButtonClick()
                }) {
                    Text(
                        text = stringResource(
                            id = profileViewmodel.btnText.value?.callText ?: R.string.empt
                        )
                    )
                }
                if (profileViewmodel.btnText.value == BtnCall.ACCEPT_REQUEST) {
                    Spacer(
                        modifier = Modifier.width(
                            10.dp
                        )
                    )
                    Button(modifier = Modifier.weight(1f, false), onClick = {
                        profileViewmodel.cancelRequest()
                    }) {
                        Text(
                            text = stringResource(
                                id = R.string.reject_request
                            )
                        )
                    }
                }
            }
        }
    }

    if (state == ProfileViewmodel.ProfileUiState.SendMessage) {
        userData?.let {
            val encodedUrl = URLEncoder.encode(
                userData.image.ifBlank { DUMMY_URL }, StandardCharsets.UTF_8.toString()
            )
            navActions.navigateToChat(userData.id, userData.name, encodedUrl)
            profileViewmodel.setIdle()
        }
    }

    if (state is ProfileViewmodel.ProfileUiState.Error) {
        context.showToast(txt = state.e)
        profileViewmodel.setIdle()
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
//    ProfileContent(
//        ProfileViewmodel(ProfileRepository()), navActions = HomeNavigationActions(
//            rememberNavController()
//        )
//    )
}