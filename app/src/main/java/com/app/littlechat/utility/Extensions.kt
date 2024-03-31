package com.app.littlechat.utility

import android.app.Activity

import android.content.Context
import android.content.ContextWrapper
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

fun Context.finishActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()