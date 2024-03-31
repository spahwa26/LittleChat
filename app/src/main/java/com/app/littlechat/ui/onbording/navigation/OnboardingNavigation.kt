package com.app.littlechat.ui.onbording.navigation

import androidx.navigation.NavHostController
import com.app.littlechat.ui.onbording.navigation.LittleChatDestinations.LOGIN_ROUTE
import com.app.littlechat.ui.onbording.navigation.LittleChatDestinations.ONBOARDING_ROUTE
import com.app.littlechat.ui.onbording.navigation.LittleChatDestinations.SIGNUP_ROUTE
import com.app.littlechat.ui.onbording.navigation.LittleChatScreen.LOGIN_SCREEN
import com.app.littlechat.ui.onbording.navigation.LittleChatScreen.ONBOARDING_SCREEN
import com.app.littlechat.ui.onbording.navigation.LittleChatScreen.SIGNUP_SCREEN

private object LittleChatScreen {
    const val ONBOARDING_SCREEN = "onboarding"
    const val LOGIN_SCREEN = "login"
    const val SIGNUP_SCREEN = "signup"
}

object LittleChatArgs{

}

object LittleChatDestinations{
    const val ONBOARDING_ROUTE=ONBOARDING_SCREEN
    const val LOGIN_ROUTE=LOGIN_SCREEN
    const val SIGNUP_ROUTE=SIGNUP_SCREEN
}

class LittleChatNavigationActions(private val navController: NavHostController) {
    fun navigateToOnboarding(){
        navController.navigate(ONBOARDING_ROUTE)
    }
    fun navigateToLogin(){
        navController.navigate(LOGIN_ROUTE)
    }
    fun navigateToSignup(){
        navController.navigate(SIGNUP_ROUTE)
    }
}