package com.app.littlechat.ui.home.navigation

import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.app.littlechat.ui.home.navigation.HomeArgs.CHAT_ID_ARG
import com.app.littlechat.ui.home.navigation.HomeArgs.GROUP_ID_ARG
import com.app.littlechat.ui.home.navigation.HomeArgs.IMAGE_ARG
import com.app.littlechat.ui.home.navigation.HomeArgs.NAME_ARG
import com.app.littlechat.ui.home.navigation.HomeArgs.USER_ID_ARG
import com.app.littlechat.ui.home.navigation.HomeScreens.CHATS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.CREATE_GROUP_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.FIND_FRIENDS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.FRIENDS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.FRIEND_REQUEST_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.GROUPS_CHAT_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.GROUPS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.PROFILE_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.SETTINGS_SCREEN

private object HomeScreens {
    const val FRIENDS_SCREEN = "friends"
    const val GROUPS_SCREEN = "groups"
    const val SETTINGS_SCREEN = "settings"
    const val CHATS_SCREEN = "chats"
    const val GROUPS_CHAT_SCREEN = "group_chats"
    const val FIND_FRIENDS_SCREEN = "find_friends"
    const val PROFILE_SCREEN = "profile"
    const val FRIEND_REQUEST_SCREEN = "friend_requests"
    const val CREATE_GROUP_SCREEN = "create_group"
}

object HomeArgs {
    const val CHAT_ID_ARG = "chatId"
    const val IMAGE_ARG = "image"
    const val NAME_ARG = "name"
    const val USER_ID_ARG = "userId"
    const val GROUP_ID_ARG = "group_id"
}

object HomeDestinations {
    const val FRIENDS_ROUTE = FRIENDS_SCREEN
    const val GROUPS_ROUTE = GROUPS_SCREEN
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
    const val CHATS_ROUTE = "$CHATS_SCREEN/{$CHAT_ID_ARG}/{$NAME_ARG}/{$IMAGE_ARG}"
    const val GROUP_CHAT_ROUTE = "$GROUPS_CHAT_SCREEN/{$CHAT_ID_ARG}/{$NAME_ARG}/{$IMAGE_ARG}"
    const val FIND_FRIENDS_ROUTE = FIND_FRIENDS_SCREEN
    const val PROFILE_ROUTE = "$PROFILE_SCREEN/{$USER_ID_ARG}"
    const val FRIEND_REQUEST_ROUTE = FRIEND_REQUEST_SCREEN
    const val CREATE_GROUP_ROUTE = "$CREATE_GROUP_SCREEN/{$GROUP_ID_ARG}"
}


class HomeNavigationActions(val navController: NavHostController) {
    fun navigateToChat(id: String, name: String, image: String) {
        navController.navigate("$CHATS_SCREEN/$id/$name/$image")
    }

    fun navigateToGroupChat(id: String, name: String, image: String) {
        navController.navigate("$GROUPS_CHAT_SCREEN/$id/$name/$image")
    }

    fun navigateToProfile(id: String) {
        navController.navigate("$PROFILE_SCREEN/$id")
    }

    fun navigateToFriendRequests() {
        navController.navigate(FRIEND_REQUEST_SCREEN)
    }

    fun popBack(): Boolean {
        navController.popBackStack()
        return false
    }

    fun navigateToFindFriends() {
        navController.navigate(HomeDestinations.FIND_FRIENDS_ROUTE)
    }

    fun navigateToCreateGroup(groupId: String? = null) {
        navController.navigate("$CREATE_GROUP_SCREEN/$groupId")
    }

    fun navigateBottomBar(route: String) {
        navController.navigate(route, navOptions = navOptions {
            launchSingleTop = true
            popUpTo(HomeDestinations.FRIENDS_ROUTE)
        })
    }
}