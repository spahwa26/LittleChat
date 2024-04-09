package com.app.littlechat.data.network

import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.Constants.Companion.CHATS
import com.app.littlechat.utility.Constants.Companion.FRIENDS
import com.app.littlechat.utility.Constants.Companion.GROUPS
import com.app.littlechat.utility.Constants.Companion.MESSAGES
import com.app.littlechat.utility.Constants.Companion.MY_GROUPS
import com.app.littlechat.utility.Constants.Companion.PARTICIPANTS
import com.app.littlechat.utility.Constants.Companion.USERS
import com.app.littlechat.utility.SomethingWentWrongException
import com.app.littlechat.utility.setError
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class ChatRepository @Inject constructor(private val userPreferences: UserPreferences) :
    CommonRepo() {
    var isGroupChat = false

    private var chatListener: ChildEventListener? = null

    private var ref: DatabaseReference? = null

    fun setChatListener(
        chatID: String,
        resultCallback: (CustomResult<Chat?>) -> Unit
    ) {
        if (chatListener != null) {
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


    fun removeListeners() {
        chatListener?.let {
            ref?.removeEventListener(it)
            chatListener = null
        }
    }


    fun removeFriend(friendId: String, resultCallback: (CustomResult<Unit>) -> Unit) {
        userPreferences.id?.let { myId ->
            db.child(FRIENDS).child(myId).child(friendId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.child(FRIENDS).child(friendId).child(myId).removeValue()
                        resultCallback.invoke(CustomResult.Success(Unit))
                    } else
                        setError(resultCallback, task.exception?.message)
                }.addOnFailureListener { e ->
                    setError(resultCallback, e.message)
                }
        }
    }


    fun leaveGroup(
        groupId: String,
        resultCallback: (CustomResult<Unit>) -> Unit
    ) {
        userPreferences.id?.let { myId ->
            db.child(USERS).child(myId).child(MY_GROUPS).child(groupId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.child(GROUPS).child(groupId).child(PARTICIPANTS).child(myId)
                            .removeValue()
                        resultCallback.invoke(CustomResult.Success(Unit))
                    } else
                        setError(resultCallback, task.exception?.message)
                }.addOnFailureListener { e ->
                    setError(resultCallback, e.message)
                }
        }
    }


    fun deleteGroup(
        groupId: String, members: List<User>,
        resultCallback: (CustomResult<Unit>) -> Unit
    ) {
        userPreferences.id?.let { myId ->
            db.child(GROUPS).child(groupId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        members.forEach {
                            db.child(USERS).child(it.id).child(MY_GROUPS).child(groupId).removeValue()
                        }
                        resultCallback.invoke(CustomResult.Success(Unit))
                    } else
                        setError(resultCallback, task.exception?.message)
                }.addOnFailureListener { e ->
                    setError(resultCallback, e.message)
                }
        }
    }
}