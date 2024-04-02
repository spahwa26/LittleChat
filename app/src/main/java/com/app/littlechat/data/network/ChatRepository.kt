package com.app.littlechat.data.network

import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.SomethingWentWrongException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class ChatRepository @Inject constructor(private val userPreferences: UserPreferences) {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }

    fun setChatListener(chatID: String, resultCallback: (CustomResult<Chat?>) -> Unit){
        db.child(Constants.CHATS).child(chatID).child("messages").addChildEventListener(object:
            ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.value != null) {
                    try {
                        resultCallback.invoke(CustomResult.Success(snapshot.getValue(Chat::class.java)))
                    }
                    catch (_: Exception){ }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendMessage(
        chat: Chat,
        chatID: String,
        resultCallback: (CustomResult<List<Chat>>) -> Unit
    ) {
        db.child(Constants.CHATS).child("$chatID/messages").push().setValue(chat)
            .addOnCompleteListener {
                if (it.exception != null)
                    resultCallback.invoke(CustomResult.Error(exception = LocalisedException(it.exception?.message)))
                else if (it.isCanceled)
                    resultCallback.invoke(CustomResult.Error(exception = SomethingWentWrongException()))
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