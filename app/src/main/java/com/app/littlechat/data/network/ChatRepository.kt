package com.app.littlechat.data.network

import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.utility.Constants
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class ChatRepository @Inject constructor(private val userPreferences: UserPreferences) {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }


    fun getChats(chatID: String, resultCallback: (CustomResult<List<Chat>>) -> Unit) {
        val chatList = mutableListOf<Chat>()
        db.child(Constants.CHATS).child(chatID).child("messages").get()
            .addOnCompleteListener {
                val dataSnapshot = it.result
                if (dataSnapshot.value != null) {
                    try {
                        for (chat in dataSnapshot.children) {
                            chat?.getValue(Chat::class.java)?.let { msg ->
                                chatList.add(
                                    msg
                                )
                            }
                        }
                        resultCallback.invoke(CustomResult.Success(chatList))

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
    }


//    private fun removeFriend(pos: Int) {
//        CommonUtilities.showProgressWheel(activity)
//        val id = friendList.get(pos).id
//        FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(userID)?.child(id)
//            ?.removeValue()?.addOnCompleteListener { task ->
//                CommonUtilities.hideProgressWheel()
//                if (task.isSuccessful) {
//                    FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(id)
//                        ?.child(userID)?.removeValue()
//                } else
//                    CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
//            }?.addOnFailureListener { e ->
//                CommonUtilities.hideProgressWheel()
//                CommonUtilities.showAlert(activity, e.message, false, true)
//            }
//    }

}