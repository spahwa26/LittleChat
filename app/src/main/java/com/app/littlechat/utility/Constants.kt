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
        const val IMAGE_MIME = "image/*"
        const val PROFILE_PIC = "profile_pic.jpeg"
        const val GROUP_ICON = "group_icon.jpeg"
        const val FIREBASE_STORAGE_PATH = "images/"


        //Find friends error constants
        const val EMPTY_LIST = 6


        //Request constants
        const val FRIEND_LIST = 7
        const val REQUEST_LIST = 8
        const val GET_USER = 9
        //const val FRIEND_LIST = 7

        //Response constant
        const val SHOW_SEND_MESSAGE = 10
        const val SHOW_SEND_REQUEST = 11
        const val SHOW_ACCEPT_REQUEST = 12
        const val SHOW_CANCEL_REQUEST = 13
        const val SHOW_NO_DATA = 14
    }


}