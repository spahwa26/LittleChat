package com.app.littlechat.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.BottomNavItem
import com.app.littlechat.ui.home.navigation.HomeDestinations.FRIENDS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.GROUPS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeDestinations.SETTINGS_ROUTE
import com.app.littlechat.ui.home.navigation.HomeNavGraph
import com.app.littlechat.ui.home.navigation.HomeNavigationActions
import com.app.littlechat.ui.theme.LittleChatTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(userPreferences)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContent(userPreferences: UserPreferences) {
    val bottomBarVisibilityState = rememberSaveable {
        mutableStateOf(true)
    }
    val floatingVisibilityState = rememberSaveable {
        mutableStateOf(true)
    }
    val dynamicThemeEnabled = rememberSaveable {
        mutableStateOf(userPreferences.isDynamicTheme)
    }
    val invertTheme = rememberSaveable {
        mutableStateOf(userPreferences.invertTheme)
    }

    val navController = rememberNavController()
    val navActions = remember(navController) {
        HomeNavigationActions(navController)
    }
    LittleChatTheme(dynamicColor = dynamicThemeEnabled, invertTheme = invertTheme) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(floatingActionButton = {
                FloatingButton(navActions, floatingVisibilityState)
            }, bottomBar = {
                BottomNavigationBar(
                    items = listOf(
                        BottomNavItem(
                            stringResource(id = R.string.friends),
                            FRIENDS_ROUTE,
                            painterResource(
                                id = R.drawable.ic_person,
                            )
                        ),
                        BottomNavItem(
                            stringResource(id = R.string.groups),
                            GROUPS_ROUTE,
                            painterResource(
                                id = R.drawable.ic_group,
                            ),
                            badgeCount = 26
                        ),
                        BottomNavItem(
                            stringResource(id = R.string.settings),
                            SETTINGS_ROUTE,
                            painterResource(
                                id = R.drawable.ic_settings,
                            )
                        )
                    ),
                    navController = navController,
                    modifier = Modifier,
                    onItemClick = {
                        navActions.navigateBottomBar(it.route)
                    },
                    bottomBarState = bottomBarVisibilityState
                )
            }) {
                if (it.calculateBottomPadding().value != 0f && userPreferences.bottomPadding == 0f) {
                    userPreferences.bottomPadding = it.calculateBottomPadding().value
                }
                HomeNavGraph(
                    modifier = Modifier.fillMaxSize(),
                    userPreferences = userPreferences,
                    bottomNavVisibilityState = bottomBarVisibilityState,
                    floatingNavVisibilityState = floatingVisibilityState,
                    enableDisableDynamicColor = dynamicThemeEnabled,
                    invertTheme = invertTheme,
                    bottomPadding = (userPreferences.bottomPadding ?: 0f).dp,
                    navActions = navActions,
                    preferences = userPreferences
                )
            }
        }
    }
}

@Composable
fun FloatingButton(
    navActions: HomeNavigationActions,
    floatingVisibilityState: MutableState<Boolean>
) {
    val backStack = navActions.navController.currentBackStackEntryAsState()
    AnimatedVisibility(
        visible = floatingVisibilityState.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        androidx.compose.material3.FloatingActionButton(onClick = {
            if (backStack.value?.destination?.route == FRIENDS_ROUTE) {
                navActions.navigateToFindFriends()
            }
            else if (backStack.value?.destination?.route == GROUPS_ROUTE) {
                navActions.navigateToCreateGroup()
            }
        }) {
            Text(text = "+", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier,
    onItemClick: (BottomNavItem) -> Unit,
    bottomBarState: MutableState<Boolean>
) {
    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        val backstackEntry = navController.currentBackStackEntryAsState()
        NavigationBar(modifier = modifier, tonalElevation = 10.dp) {
            items.forEach { item ->
                val selected = item.route == backstackEntry.value?.destination?.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onItemClick(item) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (item.badgeCount > 0)
                                BadgedBox(badge = {
                                    Text(
                                        modifier = Modifier
                                            .background(
                                                color = Color.Red,
                                                shape = CircleShape
                                            )
                                            .padding(horizontal = 3.dp),
                                        text = item.badgeCount.toString(),
                                        fontSize = 9.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }) {
                                    Icon(
                                        modifier = Modifier.size(25.dp),
                                        painter = item.icon,
                                        contentDescription = item.name
                                    )
                                }
                            else Icon(
                                modifier = Modifier.size(25.dp),
                                painter = item.icon,
                                contentDescription = item.name
                            )

                            Text(text = item.name)
                        }
                    }
                )
            }
        }
    }

}

@Preview
@Composable
private fun MainPrev() {
    MainContent(UserPreferences(LocalContext.current))
}