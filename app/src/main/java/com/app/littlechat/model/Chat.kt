package com.app.littlechat.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

class Chat : Parcelable {

    var sender_id: String = ""
    var receiver_id: String = ""
    var sender_image: String = ""
    var sender_name: String = ""
    var message: String = ""
    var time: Long = 0
    var status: String = ""

    constructor()


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    )

    constructor(
        sender_id: String,
        receiver_id: String,
        sender_image: String,
        sender_name: String,
        message: String,
        time: Long,
        status: String
    ) {
        this.sender_id = sender_id
        this.receiver_id = receiver_id
        this.sender_image = sender_image
        this.sender_name = sender_name
        this.message = message
        this.time = time
        this.status = status
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(sender_id)
        p0.writeString(receiver_id)
        p0.writeString(sender_image)
        p0.writeString(sender_name)
        p0.writeString(message)
        p0.writeLong(time)
        p0.writeString(status)
    }


    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Chat> {
            override fun createFromParcel(p0: Parcel) = Chat(p0)

            override fun newArray(p0: Int) = arrayOfNulls<Chat>(p0)

        }
    }

    override fun describeContents(): Int {
        Log.w("describeContents", "")
        return 0
    }

}