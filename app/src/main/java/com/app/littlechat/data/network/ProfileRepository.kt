package com.app.littlechat.data.network

import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.ui.home.ui.profile.BtnCall
import com.app.littlechat.utility.Constants.Companion.ACCEPTED
import com.app.littlechat.utility.Constants.Companion.FRIENDS
import com.app.littlechat.utility.Constants.Companion.FRIEND_LIST
import com.app.littlechat.utility.Constants.Companion.RECEIVED
import com.app.littlechat.utility.Constants.Companion.REQUESTS
import com.app.littlechat.utility.Constants.Companion.REQUEST_LIST
import com.app.littlechat.utility.Constants.Companion.SENT
import com.app.littlechat.utility.Constants.Companion.USERS
import com.app.littlechat.utility.setError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val userPreferences: UserPreferences) {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }


    fun getProfileData(id: String, resultCallback: (CustomResult<User>) -> Unit) {

        db.child(USERS).child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    try {
                        dataSnapshot.getValue(User::class.java)?.let {
                            resultCallback.invoke(CustomResult.Success(it))
                        }
                    } catch (e: Exception) {
                        setError(resultCallback, e.message)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                setError(resultCallback, databaseError.message)
            }
        })
    }


    fun findUserInRequestList(
        otherUserId: String,
        reqType: Int,
        resultCallback: (CustomResult<BtnCall>) -> Unit
    ) {
        val ref = when (reqType) {
            FRIEND_LIST -> db.child(FRIENDS).child(userPreferences.id ?: "").child(otherUserId)
            else -> db.child(REQUESTS).child(userPreferences.id ?: "").child(otherUserId)
        }

        ref.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            if (reqType == FRIEND_LIST) {
                                resultCallback.invoke(
                                    CustomResult.Success(BtnCall.SEND_MESSAGE)
                                )
                            } else {
                                if (user.status == RECEIVED)
                                    resultCallback.invoke(
                                        CustomResult.Success(BtnCall.ACCEPT_REQUEST)
                                    )
                                else
                                    resultCallback.invoke(
                                        CustomResult.Success(BtnCall.CANCEL_REQUEST)
                                    )
                            }
                        }
                    } else {
                        if (reqType == FRIEND_LIST) findUserInRequestList(
                            otherUserId,
                            REQUEST_LIST,
                            resultCallback
                        )
                        else resultCallback.invoke(
                            CustomResult.Success(BtnCall.SEND_REQUEST)
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    setError(resultCallback, databaseError.message)
                }
            })
    }

    fun sendRequest(friend: User, resultCallback: (CustomResult<BtnCall>) -> Unit) {
        val myData = userPreferences.myUser
        db.child(REQUESTS).child(myData.id).child(friend.id).setValue(friend.apply {
            status = SENT
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                db.child(REQUESTS).child(friend.id).child(myData.id).setValue(myData.apply {
                    status = RECEIVED
                }).addOnCompleteListener {
                    if (it.isSuccessful) {
                        resultCallback.invoke(CustomResult.Success(BtnCall.CANCEL_REQUEST))
                    }
                }

            } else
                setError(resultCallback, task.exception?.message)
        }.addOnFailureListener { e ->
            setError(resultCallback, e.message)
        }
    }

    fun cancelRequest(friendId: String, resultCallback: (CustomResult<BtnCall>) -> Unit) {
        db.child(REQUESTS).child(userPreferences.id ?: "").child(friendId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.child(REQUESTS).child(friendId).child(userPreferences.id ?: "").removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                resultCallback.invoke(CustomResult.Success(BtnCall.SEND_REQUEST))
                            }
                        }

                } else
                    setError(resultCallback, task.exception?.message)
            }.addOnFailureListener { e ->
                setError(resultCallback, e.message)
            }
    }


    fun acceptRequest(friendData: User, resultCallback: (CustomResult<BtnCall>) -> Unit) {
        userPreferences.id?.let { myId ->
            db.child(FRIENDS).child(myId).child(friendData.id).setValue(
                friendData.apply {
                    status = ACCEPTED
                }
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.child(FRIENDS).child(friendData.id).child(myId)
                        .setValue(userPreferences.myUser.apply {
                            status = ACCEPTED
                        })
                    cancelRequest(friendData.id) {
                        when (it) {
                            is CustomResult.Success -> resultCallback.invoke(CustomResult.Success(it.data))
                            is CustomResult.Error -> resultCallback.invoke(it)
                        }
                    }
                } else
                    setError(resultCallback, task.exception?.message)
            }.addOnFailureListener { e ->
                setError(resultCallback, e.message)
            }
        }

    }

}