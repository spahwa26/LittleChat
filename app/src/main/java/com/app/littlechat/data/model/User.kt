package com.app.littlechat.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    var phone_number: String = "",
    var image: String = "",
    var status: String = "",
    var isAdded: Boolean = false
) : Parcelable