package com.app.littlechat.data

import android.content.Context
import androidx.preference.PreferenceManager
import javax.inject.Inject

class UserPreferences @Inject constructor(val context: Context) {
    private val prefManager by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    var isLoggedIn: Boolean
        set(value) = prefManager.edit().putBoolean(LOGGED_IN, value).apply()
        get() = prefManager.getBoolean(LOGGED_IN, false)

    var deviceToken: String?
        get() = prefManager.getString(DEVICE_TOKEN, null)
        set(value) = prefManager.edit().putString(DEVICE_TOKEN, value).apply()

    var id: String?
        get() = prefManager.getString(ID, null)
        set(value) = prefManager.edit().putString(ID, value).apply()

    var name: String?
        get() = prefManager.getString(NAME, null)
        set(value) = prefManager.edit().putString(NAME, value).apply()

    var email: String?
        get() = prefManager.getString(EMAIL, null)
        set(value) = prefManager.edit().putString(EMAIL, value).apply()

    var phone: String?
        get() = prefManager.getString(PHONE, null)
        set(value) = prefManager.edit().putString(PHONE, value).apply()

    var image: String?
        get() = prefManager.getString(IMAGE, null)
        set(value) = prefManager.edit().putString(IMAGE, value).apply()

    var status: String?
        get() = prefManager.getString(STATUS, null)
        set(value) = prefManager.edit().putString(STATUS, value).apply()

    fun clearPrefs() {
        prefManager.edit().clear().apply()
    }

    companion object {
        const val LOGGED_IN = "LOGGED_IN"
        const val DEVICE_TOKEN = "DEVICE_TOKEN"

        const val ID = "sender_id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val IMAGE = "image"
        const val STATUS = "status"
    }
}