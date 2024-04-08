package com.app.littlechat.ui.settings

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.ui.commoncomposables.CommonAlertDialog
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.SettingsTextOption
import com.app.littlechat.ui.commoncomposables.ToggleCard
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.ui.onbording.OnboardingActivity
import com.app.littlechat.utility.finishActivity


@Composable
fun SettingsScreen(
    bottomPadding: Dp,
    userPreferences: UserPreferences,
    navActions: HomeNavigationActions,
    enableDisableDynamicColor: MutableState<Boolean>,
    invertTheme: MutableState<Boolean>
) {

    SettingsContent(
        bottomPadding,
        userPreferences,
        navActions,
        enableDisableDynamicColor,
        invertTheme
    )

}


@Composable
fun SettingsContent(
    bottomPadding: Dp,
    userPreferences: UserPreferences,
    navActions: HomeNavigationActions,
    enableDisableDynamicColor: MutableState<Boolean>,
    invertTheme: MutableState<Boolean>
) {
    val context = LocalContext.current
    val darkThemeToggle = remember {
        mutableStateOf(userPreferences.invertTheme)
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
            userPreferences.invertTheme = it
            invertTheme.value = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ToggleCard(
                text = stringResource(id = R.string.dynamic_theme),
                toggle = dynamicToggle.value
            ) {
                dynamicToggle.value = it
                userPreferences.isDynamicTheme = it
                enableDisableDynamicColor.value = it
            }
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
                context.startActivity(Intent(context, OnboardingActivity::class.java))
                context.finishActivity()
            },
            dialogTitle = stringResource(id = R.string.alert),
            dialogText = stringResource(id = R.string.logout_alert),
            confirmText = stringResource(id = R.string.yes),
            dismissText = stringResource(id = R.string.cancel)
        )
}


//@Preview(showBackground = true)
//@Composable
//private fun SettingPrev() {
//    val context = LocalContext.current
//    SettingsContent(
//        80.dp, userPreferences = UserPreferences(context = context), HomeNavigationActions(
//            rememberNavController()
//        )
//    )
//}
