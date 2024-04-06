package com.app.littlechat.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone_number: String = "",
    val image: String = "",
    var status: String = "",
    var isAdded: Boolean = false
) : Parcelable