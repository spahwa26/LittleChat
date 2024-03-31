package com.app.littlechat.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.littlechat.ui.home.ui.firends.FriendsScreen
import com.app.littlechat.ui.home.ui.groups.GroupsScreen
import com.app.littlechat.ui.home.ui.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = HomeDestinations.FRIENDS_ROUTE,
    navActions: HomeNavigationActions = remember(navController) {
        HomeNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(HomeDestinations.FRIENDS_ROUTE) {
            FriendsScreen()
        }

        composable(HomeDestinations.GROUPS_ROUTE) {
            GroupsScreen()
        }

        composable(HomeDestinations.SETTINGS_ROUTE) {
            SettingsScreen()
        }

    }

}