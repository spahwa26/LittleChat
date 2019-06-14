package com.app.littlechat.pojo

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

class Chat : Parcelable{

    var id: String=""
    var message: String=""
    var time : Long=0
    var status:String=""

    constructor()

    constructor(id: String, message: String, time: Long, status: String) {
        this.id=id
        this.message = message
        this.time = time
        this.status = status
    }


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString()
    )

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(id)
        p0.writeString(message)
        p0.writeLong(time)
        p0.writeString(status)
    }


    companion object{
        @JvmField
        val CREATOR = object  : Parcelable.Creator<Chat>{
            override fun createFromParcel(p0: Parcel) = Chat(p0)

            override fun newArray(p0: Int) = arrayOfNulls<Chat>(p0)

        }
    }

    override fun describeContents(): Int {
        Log.w("describeContents", "")
        return 0
    }

}