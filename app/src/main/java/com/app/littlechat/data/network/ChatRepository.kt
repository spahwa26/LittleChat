package com.app.littlechat.data.network

import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.Constants.Companion.CHATS
import com.app.littlechat.utility.Constants.Companion.GROUPS
import com.app.littlechat.utility.Constants.Companion.MESSAGES
import com.app.littlechat.utility.Constants.Companion.PARTICIPANTS
import com.app.littlechat.utility.Constants.Companion.USERS
import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.SomethingWentWrongException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class ChatRepository @Inject constructor() {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }
    var isGroupChat = false

    private var chatListener: ChildEventListener? = null

    private var ref: DatabaseReference? = null

    fun setChatListener(
        chatID: String,
        resultCallback: (CustomResult<Chat?>) -> Unit
    ) {
        if(chatListener!=null){
            return
        }
        ref = if (isGroupChat) db.child(GROUPS).child(chatID).child(MESSAGES)
            else db.child(CHATS).child(chatID).child(MESSAGES)
        chatListener = ref?.addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.value != null) {
                    try {
                        resultCallback.invoke(CustomResult.Success(snapshot.getValue(Chat::class.java)))
                    } catch (_: Exception) {
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                removeListeners()
            }
        })
    }

    fun sendMessage(
        chat: Chat,
        chatID: String,
        resultCallback: (CustomResult<List<Chat>>) -> Unit
    ) {
        db.child(if (isGroupChat) GROUPS else CHATS).child("$chatID/$MESSAGES").push()
            .setValue(chat)
            .addOnCompleteListener {
                if (it.exception != null)
                    setError(resultCallback, it.exception?.message)
                else if (it.isCanceled)
                    resultCallback.invoke(CustomResult.Error(exception = SomethingWentWrongException()))
            }
    }

    fun getParticipantsData(
        groupId: String,
        resultCallback: (CustomResult<List<User>>) -> Unit
    ) {

        val participantsList = mutableListOf<User>()
        db.child(GROUPS).child(groupId)
            .child(PARTICIPANTS).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    if (dataSnapshot.value != null) {
                        try {
                            participantsList.clear()
                            for (ids in dataSnapshot.children) {
                                ids.getValue(String::class.java)?.let { id ->
                                    db.child(USERS).child(id).get().addOnCompleteListener { task1 ->
                                        if (task1.isSuccessful) {
                                            val userSnapshot = task1.result
                                            userSnapshot.getValue(User::class.java)?.let { user ->
                                                participantsList.add(user)
                                            }
                                            if (ids.key.equals(dataSnapshot.children.last().key)) {
                                                resultCallback.invoke(
                                                    CustomResult.Success(
                                                        participantsList
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                            }

                        } catch (e: Exception) {
                            setError(resultCallback, e.message)
                        }

                    }
                }
            }

    }

    private fun <T> setError(resultCallback: (CustomResult<List<T>>) -> Unit, e: String? = null) {
        resultCallback.invoke(
            CustomResult.Error(
                exception = LocalisedException(e)
            )
        )
    }

    fun removeListeners() {
        chatListener?.let {
            ref?.removeEventListener(it)
            chatListener=null
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