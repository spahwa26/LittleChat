package com.app.littlechat.ui.home.navigation

import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.app.littlechat.ui.home.navigation.HomeArgs.CHAT_ID_ARG
import com.app.littlechat.ui.home.navigation.HomeArgs.IMAGE_ARG
import com.app.littlechat.ui.home.navigation.HomeArgs.NAME_ARG
import com.app.littlechat.ui.home.navigation.HomeDestinations.FIND_FRIENDS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.FRIENDS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.GROUPS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.SETTINGS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeScreens.CHATS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.FIND_FRIENDS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.FRIENDS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.GROUPS_CHAT_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.GROUPS_SCREEN
import com.app.littlechat.ui.home.navigation.HomeScreens.SETTINGS_SCREEN

private object HomeScreens {
    const val FRIENDS_SCREEN = "friends"
    const val GROUPS_SCREEN = "groups"
    const val SETTINGS_SCREEN = "settings"
    const val CHATS_SCREEN = "chats"
    const val GROUPS_CHAT_SCREEN = "group_chats"
    const val FIND_FRIENDS_SCREEN = "find_friends"
}

object HomeArgs{
    const val CHAT_ID_ARG = "chatId"
    const val IMAGE_ARG = "image"
    const val NAME_ARG = "name"
}

object HomeDestinations{
    const val FRIENDS_ROUTE=FRIENDS_SCREEN
    const val GROUPS_ROUTE=GROUPS_SCREEN
    const val SETTINGS_ROUTE=SETTINGS_SCREEN
    const val CHATS_ROUTE="$CHATS_SCREEN/{$CHAT_ID_ARG}/{$NAME_ARG}/{$IMAGE_ARG}"
    const val GROUP_CHAT_ROUTE="$GROUPS_CHAT_SCREEN/{$CHAT_ID_ARG}/{$NAME_ARG}/{$IMAGE_ARG}"
    const val FIND_FRIENDS_ROUTE=FIND_FRIENDS_SCREEN
}

class HomeNavigationActions(private val navController: NavHostController) {
    fun navigateToChat(id: String, name: String, image: String){
        navController.navigate("$CHATS_SCREEN/$id/$name/$image")
    }
    fun navigateToGroupChat(id: String, name: String, image: String){
        navController.navigate("$GROUPS_CHAT_SCREEN/$id/$name/$image")
    }

    fun popBack(){
        navController.popBackStack()
    }
}