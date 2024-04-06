package com.app.littlechat.utility

class Constants {

    companion object {

        const val SENT = "sent"
        const val RECEIVED = "received"
        const val REQUESTS = "requests"
        const val ACCEPTED = "accepted"
        const val FRIENDS = "friends"
        const val CHATS = "chats"
        const val GROUPS = "groups"
        const val MESSAGES = "messages"
        const val PARTICIPANTS = "participants"
        const val USERS = "users"
        const val ID = "sender_id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val IMAGE = "image"
        const val STATUS = "status"
        const val DUMMY_URL = "https://cdn-icons-png.flaticon.com/512/64/64495.png"


        //Find friends error constants
        const val EMPTY_LIST = 6


        //ProfileScreen request constant
        const val FRIEND_LIST = 7
        const val REQUEST_LIST = 8
        const val GET_USER = 9
        //const val FRIEND_LIST = 7

        //ProfileScreen response constant
        const val SHOW_SEND_MESSAGE = 10
        const val SHOW_SEND_REQUEST = 11
        const val SHOW_ACCEPT_REQUEST = 12
        const val SHOW_CANCEL_REQUEST = 13
    }


}