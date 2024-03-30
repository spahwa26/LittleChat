package com.app.littlechat.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
class User : Parcelable{


    var id: String           =""
    var name: String          =""
    var email:String          =""
    var phone_number:String    =""
    var image:String         =""
    var status:String=""
    var isAdded=false

    constructor()

    constructor(id: String, name: String, email: String, phone_number: String, image: String, status: String) {
        this.id=id
        this.name = name
        this.email = email
        this.phone_number = phone_number
        this.image=image
        this.status=status
    }


    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(id)
        p0.writeString(name)
        p0.writeString(email)
        p0.writeString(phone_number)
        p0.writeString(image)
        p0.writeString(status)
    }


    companion object{
        @JvmField
        val CREATOR = object  : Parcelable.Creator<User>{
            override fun createFromParcel(p0: Parcel) = User(p0)

            override fun newArray(p0: Int) = arrayOfNulls<User>(p0)

        }
    }

    override fun describeContents(): Int {
        Log.w("describeContents", "")
        return 0
    }


}