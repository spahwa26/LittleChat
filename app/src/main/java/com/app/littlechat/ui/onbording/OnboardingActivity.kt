package com.app.littlechat.ui.onbording

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.app.littlechat.LittleChatNavGraph
import com.nickelfox.shoppingportal.ui.theme.LittleChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             MaterialTheme {
                 Surface(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground)) {
                     LittleChatNavGraph()
                 }
             }
//            LittleChatTheme {
//                Surface (
//                    modifier = Modifier.fillMaxSize()
//                ){
//                }
//            }
        }
    }

}