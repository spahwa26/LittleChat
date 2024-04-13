package com.app.littlechat.ui.onbording

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.ui.home.HomeActivity
import com.app.littlechat.ui.theme.LittleChatTheme
import com.app.littlechat.ui.onbording.navigation.LittleChatNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    private val viewModel by viewModels<SplashViewModel>()

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !viewModel.isReady.value
            }
        }
        if (userPreferences.id != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            setContent {
                val dynamicThemeEnabled = rememberSaveable {
                    mutableStateOf(userPreferences.isDynamicTheme)
                }
                val invertTheme = rememberSaveable {
                    mutableStateOf(userPreferences.invertTheme)
                }
                LittleChatTheme(dynamicColor = dynamicThemeEnabled, invertTheme = invertTheme) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onBackground)
                    ) {
                        LittleChatNavGraph()
                    }
                }
            }
        }
    }

}