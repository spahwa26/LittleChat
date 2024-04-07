package com.app.littlechat.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.ui.commoncomposables.CommonAlertDialog
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.SettingsTextOption
import com.app.littlechat.ui.commoncomposables.ToggleCard
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.utility.finishActivity


@Composable
fun SettingsScreen(
    bottomPadding: Dp,
    userPreferences: UserPreferences,
    navActions: HomeNavigationActions
) {

    SettingsContent(bottomPadding, userPreferences, navActions)

}


@Composable
fun SettingsContent(
    bottomPadding: Dp,
    userPreferences: UserPreferences,
    navActions: HomeNavigationActions
) {
    val context = LocalContext.current
    val darkThemeToggle = remember {
        mutableStateOf(userPreferences.isDarkTheme)
    }
    val dynamicToggle = remember {
        mutableStateOf(userPreferences.isDynamicTheme)
    }
    val showLogoutAlert = remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding)
    ) {

        CustomToolbar(title = stringResource(id = R.string.app_name))

        ToggleCard(
            text = stringResource(id = R.string.invert_theme),
            toggle = darkThemeToggle.value
        ) {
            darkThemeToggle.value = it
            userPreferences.isDarkTheme = it
        }

        ToggleCard(
            text = stringResource(id = R.string.disable_dynamic_theme),
            toggle = dynamicToggle.value
        ) {
            dynamicToggle.value = it
            userPreferences.isDynamicTheme = it
        }

        SettingsTextOption(text = stringResource(id = R.string.update_profile)) {
            userPreferences.id?.let {
                navActions.navigateToProfile(it)
            }
        }

        SettingsTextOption(text = stringResource(id = R.string.friend_requests)) {
            navActions.navigateToFriendRequests()
        }

        SettingsTextOption(text = stringResource(id = R.string.logout)) {
            showLogoutAlert.value = true
        }

    }

    if (showLogoutAlert.value)
        CommonAlertDialog(
            onDismissRequest = {
                showLogoutAlert.value = false
            },
            onConfirmation = {
                userPreferences.clearPrefs()
                showLogoutAlert.value = false
                context.finishActivity()
            },
            dialogTitle = stringResource(id = R.string.alert),
            dialogText = stringResource(id = R.string.logout_alert),
            confirmText = stringResource(id = R.string.yes),
            dismissText = stringResource(id = R.string.cancel)
        )
}


@Preview(showBackground = true)
@Composable
private fun SettingPrev() {
    val context = LocalContext.current
    SettingsContent(
        80.dp, userPreferences = UserPreferences(context = context), HomeNavigationActions(
            rememberNavController()
        )
    )
}
