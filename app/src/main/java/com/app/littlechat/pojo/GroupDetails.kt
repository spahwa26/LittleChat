package com.app.littlechat.pojo

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

class GroupDetails : Parcelable {

    var name: String = ""
    var image: String = ""
    var admin: String = ""
    var created_at: Long = 0

    constructor()


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()
    )

    constructor(
            name: String,
            image: String,
            admin: String,
            created_at: Long
    ) {
        this.name = name
        this.image = image
        this.admin = admin
        this.created_at = created_at
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(name)
        p0.writeString(image)
        p0.writeString(admin)
        p0.writeLong(created_at)
    }


    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<GroupDetails> {
            override fun createFromParcel(p0: Parcel) = GroupDetails(p0)

            override fun newArray(p0: Int) = arrayOfNulls<GroupDetails>(p0)

        }
    }

    override fun describeContents(): Int {
        Log.w("describeContents", "")
        return 0
    }

}