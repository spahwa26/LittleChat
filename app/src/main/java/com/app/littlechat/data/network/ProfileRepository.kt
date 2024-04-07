package com.app.littlechat.data.network

import android.net.Uri
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.UserPreferences.Companion.DEVICE_TOKEN
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.ui.home.ui.profile.BtnCall
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants.Companion.ACCEPTED
import com.app.littlechat.utility.Constants.Companion.FIREBASE_STORAGE_PATH
import com.app.littlechat.utility.Constants.Companion.FRIENDS
import com.app.littlechat.utility.Constants.Companion.FRIEND_LIST
import com.app.littlechat.utility.Constants.Companion.PROFILE_PIC
import com.app.littlechat.utility.Constants.Companion.RECEIVED
import com.app.littlechat.utility.Constants.Companion.REQUESTS
import com.app.littlechat.utility.Constants.Companion.REQUEST_LIST
import com.app.littlechat.utility.Constants.Companion.SENT
import com.app.littlechat.utility.Constants.Companion.SHOW_NO_DATA
import com.app.littlechat.utility.Constants.Companion.USERS
import com.app.littlechat.utility.deleteImageFile
import com.app.littlechat.utility.getImageFile
import com.app.littlechat.utility.setError
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
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

    fun getRequests(resultCallback: (CustomResult<List<User>>) -> Unit) {
        userPreferences.id?.let {
            val ref = FirebaseDatabase.getInstance().reference.child(REQUESTS).child(it)
            val requestList = mutableListOf<User>()
            ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        requestList.clear()
                        if (dataSnapshot.value != null) {
                            try {
                                for (user in dataSnapshot.children) {
                                    user.getValue(User::class.java)?.let { reqUser ->
                                        if (!user.key.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                                            getProfileData(reqUser.id) { result ->
                                                if (result is CustomResult.Success) {
                                                    requestList.add(result.data.apply {
                                                        status = reqUser.status
                                                    })
                                                }
                                                if (user.key.equals(dataSnapshot.children.last().key)) {
                                                    resultCallback.invoke(
                                                        CustomResult.Success(
                                                            requestList
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

                        } else {
                            resultCallback.invoke(CustomResult.Error(SHOW_NO_DATA))
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        setError(resultCallback, databaseError.message)
                    }
                })
        }
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

    fun saveProfileChanges(
        user: User,
        isUploadImage: Boolean,
        resultCallback: (CustomResult<Unit>) -> Unit
    ) {
        if (isUploadImage) {
            uploadImage { imgResult ->
                when (imgResult) {
                    is CustomResult.Success -> {
                        user.image = imgResult.data
                        updateProfile(user, resultCallback)
                    }

                    is CustomResult.Error -> {
                        setError(resultCallback, imgResult.exception.message)
                    }
                }
            }
        } else
            updateProfile(user, resultCallback)
    }

    private fun updateProfile(user: User, resultCallback: (CustomResult<Unit>) -> Unit) {
        userPreferences.id?.let {
            db.child(USERS).child(it).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userPreferences.setUserData(user)
                    db.child(USERS).child(it).child(DEVICE_TOKEN)
                        .setValue(userPreferences.deviceToken)
//                    if (intent.hasExtra("name")) {
//                        CommonUtilities.putString(activity, "isLoggedIn", "yes")
//                        startActivity(
//                            Intent(
//                                activity,
//                                HomeScreen::class.java
//                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                        )
//                    } else
                    //CommonUtilities.showToast(activity, "Profile Updated Successfully")
                    resultCallback.invoke(CustomResult.Success(Unit))
                } else {
                    setError(resultCallback, task.exception?.message)
                }
            }.addOnFailureListener { e ->
                setError(resultCallback, e.message)
                //CommonUtilities.showAlert(activity, e.message, false, true)
            }
        }
    }

    private fun uploadImage(resultCallback: (CustomResult<String>) -> Unit) {
        val file = Uri.fromFile(userPreferences.context.getImageFile(userPreferences.profilePic))
        val storageRef = FirebaseStorage.getInstance().reference
        val riversRef = storageRef.child(FIREBASE_STORAGE_PATH + file.lastPathSegment)
        val uploadTask = riversRef.putFile(file)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    setError(resultCallback, it.message)
                }
            }
            return@Continuation riversRef.downloadUrl
        }).addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                userPreferences.context.deleteImageFile(userPreferences.profilePic)
                resultCallback.invoke(CustomResult.Success(task.result.toString()))
            } else {
                setError(resultCallback, task.exception?.message)
            }
        }
    }

}