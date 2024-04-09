package com.app.littlechat.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.ui.home.ui.HomeViewmodel
import com.app.littlechat.ui.home.ui.chat.FriendChatScreen
import com.app.littlechat.ui.home.ui.chat.GroupChatScreen
import com.app.littlechat.ui.home.ui.findfriends.FindFriendsScreen
import com.app.littlechat.ui.home.ui.firends.FriendsScreen
import com.app.littlechat.ui.home.ui.friendrequests.FriendsRequestScreen
import com.app.littlechat.ui.home.ui.group.CreateGroupScreen
import com.app.littlechat.ui.home.ui.groups.GroupsScreen
import com.app.littlechat.ui.home.ui.profile.ProfileScreen
import com.app.littlechat.ui.settings.SettingsScreen

@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
    userPreferences: UserPreferences,
    bottomNavVisibilityState: MutableState<Boolean>,
    floatingNavVisibilityState: MutableState<Boolean>,
    enableDisableDynamicColor: MutableState<Boolean>,
    invertTheme: MutableState<Boolean>,
    bottomPadding: Dp,
    startDestination: String = HomeDestinations.FRIENDS_ROUTE,
    navActions: HomeNavigationActions
) {
    //val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination
    val viewmodel: HomeViewmodel = hiltViewModel()
    NavHost(
        navController = navActions.navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(HomeDestinations.FRIENDS_ROUTE) {
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = true
                floatingNavVisibilityState.value = true
            }
            FriendsScreen(viewmodel, navActions, bottomPadding)
        }

        composable(HomeDestinations.GROUPS_ROUTE) {
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = true
                floatingNavVisibilityState.value = true
            }
            GroupsScreen(viewmodel, bottomPadding, navActions)
        }

        composable(HomeDestinations.SETTINGS_ROUTE) {
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = true
                floatingNavVisibilityState.value = false
            }
            SettingsScreen(
                bottomPadding,
                userPreferences,
                navActions,
                enableDisableDynamicColor,
                invertTheme
            )
        }

        composable(HomeDestinations.CHATS_ROUTE) {
            FriendChatScreen(navAction = navActions)
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = false
                floatingNavVisibilityState.value = false
            }
        }

        composable(HomeDestinations.GROUP_CHAT_ROUTE) {
            GroupChatScreen(navAction = navActions)
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = false
                floatingNavVisibilityState.value = false
            }
        }

        composable(HomeDestinations.FIND_FRIENDS_ROUTE) {
            FindFriendsScreen(navActions = navActions)
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = false
                floatingNavVisibilityState.value = false
            }
        }

        composable(HomeDestinations.PROFILE_ROUTE) {
            ProfileScreen(navActions = navActions)
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = false
                floatingNavVisibilityState.value = false
            }
        }

        composable(HomeDestinations.FRIEND_REQUEST_ROUTE) {
            FriendsRequestScreen(navActions = navActions)
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = false
                floatingNavVisibilityState.value = false
            }
        }

        composable(HomeDestinations.CREATE_GROUP_ROUTE) {
            CreateGroupScreen(navActions = navActions)
            LaunchedEffect(Unit) {
                bottomNavVisibilityState.value = false
                floatingNavVisibilityState.value = false
            }
        }

    }

}