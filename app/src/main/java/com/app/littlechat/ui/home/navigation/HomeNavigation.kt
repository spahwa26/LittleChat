package com.app.littlechat.ui.home.navigation

import androidx.navigation.NavHostController
import com.app.littlechat.ui.home.navigation.HomeDestinations.FRIENDS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.GROUPS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.SETTINGS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeScreens.FRIENDS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.GROUPS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.SETTINGS_SCREEN

private object HomeScreens {
    const val FRIENDS_SCREEN = "friends"
    const val GROUPS_SCREEN = "groups"
    const val SETTINGS_SCREEN = "settings"
}

object HomeArgs{

}

object HomeDestinations{
    const val FRIENDS_ROUTE=FRIENDS_SCREEN
    const val GROUPS_ROUTE=GROUPS_SCREEN
    const val SETTINGS_ROUTE=SETTINGS_SCREEN
}

class HomeNavigationActions(private val navController: NavHostController) {
    fun navigateToFriends(){
        navController.navigate(FRIENDS_ROUTE)
    }
    fun navigateToGroups(){
        navController.navigate(GROUPS_ROUTE)
    }
    fun navigateToSettings(){
        navController.navigate(SETTINGS_ROUTE)
    }
}