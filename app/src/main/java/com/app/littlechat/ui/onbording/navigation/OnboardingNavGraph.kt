package com.app.littlechat.ui.onbording.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.littlechat.ui.home.ui.profile.ProfileScreen
import com.app.littlechat.ui.onbording.LoginScreen
import com.app.littlechat.ui.onbording.OnboardingScreen
import com.app.littlechat.ui.onbording.SignupScreen

@Composable
fun LittleChatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = OnboardingDestinations.ONBOARDING_ROUTE,
    navActions: OnboardingNavigationActions = remember(navController) {
        OnboardingNavigationActions(navController)
    }
) {
//    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(OnboardingDestinations.ONBOARDING_ROUTE) {
            OnboardingScreen(onLoginClick = { navActions.navigateToLogin() }) {
                navActions.navigateToSignup()
            }
        }

        composable(OnboardingDestinations.LOGIN_ROUTE) {
            LoginScreen(onBackPress = { navController.navigateUp() }, navActions = navActions)
        }

        composable(OnboardingDestinations.SIGNUP_ROUTE) {
            SignupScreen(onBackPress = { navController.navigateUp() })
        }

        composable(OnboardingDestinations.PROFILE_ROUTE) {
            ProfileScreen()
        }

    }

}