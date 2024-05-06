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
        const val NULL = "null"
        const val GROUP_DETAILS = "group_details"
        const val MESSAGES = "messages"
        const val PARTICIPANTS = "participants"
        const val USERS = "users"
        const val MY_GROUPS = "my_groups"
        const val SENDER_ID = "sender_id"
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val IMAGE = "image"
        const val STATUS = "status"
        const val DUMMY_URL = "https://cdn-icons-png.flaticon.com/512/64/64495.png"
        const val DELETED_USER = "https://cdn2.iconfinder.com/data/icons/smiles-business/512/1040_man_with_circle_c-512.png"
        const val IMAGE_MIME = "image/*"
        const val PROFILE_PIC = "profile_pic.jpeg"
        const val GROUP_ICON = "group_icon.jpeg"
        const val FIREBASE_STORAGE_PATH = "images/"
        const val SEPARATOR = "SEPARATOR"
        fun getPicName(id: String?) = "${id}__${PROFILE_PIC}"

        //Find friends error constants
        const val EMPTY_LIST = 6


        //Request constants
        const val FRIEND_LIST = 7
        const val REQUEST_LIST = 8

        //Response constant
        const val SHOW_NO_DATA = 14

        //PREFERENCES
        const val DEVICE_TOKEN = "device_token"
        const val PHONE_NUMBER = "phone_number"
        const val BOTTOM_PADDING = "bottom_padding"
        const val DARK_THEME_TOGGLE = "DARK_THEME_TOGGLE"
        const val DYNAMIC_THEME_TOGGLE = "DYNAMIC_THEME_TOGGLE"
    }


}