package com.app.littlechat.ui.onbording

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() :
    ViewModel() {
        val isReady = mutableStateOf(false)
        init {
            viewModelScope.launch {
                delay(1200)
                isReady.value=true
            }
        }
}