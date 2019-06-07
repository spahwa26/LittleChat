package com.app.littlechat.pojo

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
class User : Serializable{

    var name: String=""
    var email:String=""
    var phone_number:String=""

    constructor(name: String, email: String, phone_number: String) {
        this.name = name
        this.email = email
        this.phone_number = phone_number
    }

    constructor()


}