package com.app.littlechat.ui.onbording.navigation

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
import com.app.littlechat.ui.onbording.LoginScreen
import com.app.littlechat.ui.onbording.OnboardingScreen
import com.app.littlechat.ui.onbording.SignupScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun LittleChatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = LittleChatDestinations.ONBOARDING_ROUTE,
    navActions: LittleChatNavigationActions = remember(navController) {
        LittleChatNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(LittleChatDestinations.ONBOARDING_ROUTE) {
            OnboardingScreen(onLoginClick = { navActions.navigateToLogin() }) {
                navActions.navigateToSignup()
            }
        }

        composable(LittleChatDestinations.LOGIN_ROUTE) {
            LoginScreen(onHomeClick = {
                navController.navigateUp()
            },
                onBackPress = { navController.navigateUp() })
        }

        composable(LittleChatDestinations.SIGNUP_ROUTE) {
            SignupScreen(onBackPress = { navController.navigateUp() })
        }

    }

}