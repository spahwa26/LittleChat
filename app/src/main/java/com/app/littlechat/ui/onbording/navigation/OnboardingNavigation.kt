package com.app.littlechat.ui.onbording.navigation

import androidx.navigation.NavHostController
import com.app.littlechat.ui.onbording.navigation.OnboardingArgs.EMAIL_ARGS
import com.app.littlechat.ui.onbording.navigation.OnboardingArgs.MY_ID_ARG
import com.app.littlechat.ui.onbording.navigation.OnboardingDestinations.LOGIN_ROUTE
import com.app.littlechat.ui.onbording.navigation.OnboardingDestinations.ONBOARDING_ROUTE
import com.app.littlechat.ui.onbording.navigation.OnboardingDestinations.SIGNUP_ROUTE
import com.app.littlechat.ui.onbording.navigation.OnboardingScreen.LOGIN_SCREEN
import com.app.littlechat.ui.onbording.navigation.OnboardingScreen.ONBOARDING_SCREEN
import com.app.littlechat.ui.onbording.navigation.OnboardingScreen.PROFILE_SCREEN
import com.app.littlechat.ui.onbording.navigation.OnboardingScreen.SIGNUP_SCREEN
import com.app.littlechat.utility.Constants.Companion.SEPARATOR

private object OnboardingScreen {
    const val ONBOARDING_SCREEN = "onboarding"
    const val LOGIN_SCREEN = "login"
    const val SIGNUP_SCREEN = "signup"
    const val PROFILE_SCREEN = "profile"
}

object OnboardingArgs {
    const val MY_ID_ARG = "userId"
    const val EMAIL_ARGS = "email"
}

object OnboardingDestinations {
    const val ONBOARDING_ROUTE = ONBOARDING_SCREEN
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val SIGNUP_ROUTE = SIGNUP_SCREEN
    const val PROFILE_ROUTE = "${PROFILE_SCREEN}/{${MY_ID_ARG}}/{${EMAIL_ARGS}}"
}

class OnboardingNavigationActions(private val navController: NavHostController) {
    fun navigateToOnboarding() {
        navController.navigate(ONBOARDING_ROUTE)
    }

    fun navigateToLogin() {
        navController.navigate(LOGIN_ROUTE)
    }

    fun navigateToSignup() {
        navController.navigate(SIGNUP_ROUTE)
    }

    fun navigateToProfile(concatString: String) {
        val id = concatString.split(SEPARATOR)[0]
        val email = concatString.split(SEPARATOR)[1]
        navController.navigate("${PROFILE_SCREEN}/$id/$email")
    }

}