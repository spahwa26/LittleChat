package com.app.littlechat.pojo

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
class Friends() : Parcelable {



    var id: String=""
    var name: String=""
    var email:String=""
    var phone_number:String=""
    var image:String=""
    var status:String=""


    constructor(id: String, name: String, email: String, phone_number: String, image: String, status: String) : this() {
        this.id=id
        this.name = name
        this.email = email
        this.phone_number = phone_number
        this.image=image
        this.status=status
    }




}