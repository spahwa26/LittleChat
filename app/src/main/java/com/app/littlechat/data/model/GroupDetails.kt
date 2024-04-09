package com.app.littlechat.data.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupDetails(
    var id: String = "",
    var name: String = "",
    var image: String = "",
    var admin: String = "",
    var created_at: Long = 0
) : Parcelable
