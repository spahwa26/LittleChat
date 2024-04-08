package com.app.littlechat.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.preference.PreferenceManager
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.Constants.Companion.BOTTOM_PADDING
import com.app.littlechat.utility.Constants.Companion.DARK_THEME_TOGGLE
import com.app.littlechat.utility.Constants.Companion.DEVICE_TOKEN
import com.app.littlechat.utility.Constants.Companion.DYNAMIC_THEME_TOGGLE
import com.app.littlechat.utility.Constants.Companion.EMAIL
import com.app.littlechat.utility.Constants.Companion.ID
import com.app.littlechat.utility.Constants.Companion.IMAGE
import com.app.littlechat.utility.Constants.Companion.NAME
import com.app.littlechat.utility.Constants.Companion.PHONE
import com.app.littlechat.utility.Constants.Companion.STATUS
import javax.inject.Inject

//todo: handle null check wherever the profile params are being used
//todo: check usage of each variable and delete unnecessary
class UserPreferences @Inject constructor(val context: Context) {
    private val prefManager by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private val prefManagerPersist by lazy {
        context.getSharedPreferences(
            "${context.packageName}_persist",
            MODE_PRIVATE
        )
    }

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

    var invertTheme: Boolean
        get() = prefManagerPersist.getBoolean(DARK_THEME_TOGGLE, false)
        set(value) = prefManagerPersist.edit().putBoolean(DARK_THEME_TOGGLE, value).apply()

    var isDynamicTheme: Boolean
        get() = prefManagerPersist.getBoolean(DYNAMIC_THEME_TOGGLE, true)
        set(value) = prefManagerPersist.edit().putBoolean(DYNAMIC_THEME_TOGGLE, value).apply()

    val profilePic = "${id}_${Constants.PROFILE_PIC}"

    val groupPic = "${id}_${Constants.GROUP_ICON}"

    fun setUserData(pojo: User?) {
        id = pojo?.id
        name = pojo?.name
        email = pojo?.email
        phone = pojo?.phone_number
        image = pojo?.image
        status = pojo?.status
    }

    var myUser = User(id ?: "", name ?: "", email ?: "", phone ?: "", image ?: "")

    fun clearPrefs() {
        prefManager.edit().clear().apply()
    }
}