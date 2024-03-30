package com.app.littlechat.utility

import android.app.Activity

import android.content.Context
import android.support.annotation.StringRes
import android.text.TextUtils
import android.widget.Toast

fun Activity.getActivity() = this

fun Context.showToast(
    @StringRes intRes: Int? = null,
    txt: String? = null,
    length: Int = Toast.LENGTH_SHORT
) {
    val toastMsg = if (intRes != null) getString(intRes) else txt
    if (toastMsg != null) {
        var newLength = length
        if (toastMsg.length > 50) newLength = Toast.LENGTH_LONG
        Toast.makeText(this, toastMsg, newLength).show()
    }
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()