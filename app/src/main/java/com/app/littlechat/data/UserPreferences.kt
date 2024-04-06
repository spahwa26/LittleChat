package com.app.littlechat.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.preference.PreferenceManager
import com.app.littlechat.data.model.User
import javax.inject.Inject

//todo: handle null check wherever the profile params are being used
class UserPreferences @Inject constructor(val context: Context) {
    private val prefManager by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private val prefManagerPersist by lazy {
        context.getSharedPreferences(
            "${context.packageName}_persist",
            MODE_PRIVATE
        )
    }

    var isLoggedIn: Boolean
        set(value) = prefManager.edit().putBoolean(LOGGED_IN, value).apply()
        get() = prefManager.getBoolean(LOGGED_IN, false)

    var deviceToken: String?
        get() = prefManagerPersist.getString(DEVICE_TOKEN, null)
        set(value) = prefManagerPersist.edit().putString(DEVICE_TOKEN, value).apply()

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

    var bottomPadding: Float?
        get() = prefManagerPersist.getFloat(BOTTOM_PADDING, 0f)
        set(value) = prefManagerPersist.edit().putFloat(BOTTOM_PADDING, value ?: 0f).apply()

    var isDarkTheme: Boolean
        get() = prefManagerPersist.getBoolean(DARK_THEME_TOGGLE, false)
        set(value) = prefManagerPersist.edit().putBoolean(DARK_THEME_TOGGLE, value).apply()

    var isDynamicTheme: Boolean
        get() = prefManagerPersist.getBoolean(DYNAMIC_THEME_TOGGLE, true)
        set(value) = prefManagerPersist.edit().putBoolean(DYNAMIC_THEME_TOGGLE, value).apply()

    val myUser = User(id ?: "", name ?: "", email ?: "", phone ?: "", image ?: "")

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
        const val BOTTOM_PADDING = "bottom_padding"
        const val DARK_THEME_TOGGLE = "DARK_THEME_TOGGLE"
        const val DYNAMIC_THEME_TOGGLE = "DYNAMIC_THEME_TOGGLE"
    }
}