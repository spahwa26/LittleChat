package com.app.littlechat.ui.home.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp


@Composable
fun SettingsScreen(bottomPadding: Dp) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)){
        Text(text = "Settings Screen")
    }
}