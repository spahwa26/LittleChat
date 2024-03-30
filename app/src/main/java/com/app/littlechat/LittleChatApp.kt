package com.app.littlechat

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LittleChatApp : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
    }
}