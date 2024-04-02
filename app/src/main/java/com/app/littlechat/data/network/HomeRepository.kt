package com.app.littlechat.data.network

import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.GroupDetails
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.SomethingWentWrongException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepository @Inject constructor(private val userPreferences: UserPreferences) {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }

    fun getFriends(resultCallback: (CustomResult<List<User>>) -> Unit) {
        userPreferences.id?.let {
            val friendList = mutableListOf<User>()
            val ref = db.child(Constants.FRIENDS).child(it)
            ref.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null) {
                            try {
                                for (user in dataSnapshot.children) {
                                    user.getValue(User::class.java)?.let { friend ->
                                        if (user.key.equals(dataSnapshot.children.last().key))
                                            getUsersData(
                                                user.key ?: "",
                                                true,
                                                friend.status,
                                                friendList,
                                                resultCallback
                                            )
                                        else
                                            getUsersData(
                                                user.key ?: "",
                                                false,
                                                friend.status,
                                                friendList,
                                                resultCallback
                                            )
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            resultCallback.invoke(
                                CustomResult.Error(
                                    exception = SomethingWentWrongException()
                                )
                            )
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        resultCallback.invoke(
                            CustomResult.Error(
                                exception = LocalisedException(
                                    databaseError.message
                                )
                            )
                        )
                    }
                })
        }
    }

    private fun getUsersData(
        id: String,
        notify: Boolean,
        status: String,
        friendList: MutableList<User>,
        resultCallback: (CustomResult<List<User>>) -> Unit
    ) {
        db.child("users/$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        try {
                            dataSnapshot.getValue(User::class.java)?.let { user ->
                                user.status = status
                                friendList.add(user)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (notify)
                        resultCallback.invoke(CustomResult.Success(friendList))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    resultCallback.invoke(
                        CustomResult.Error(
                            exception = LocalisedException(
                                databaseError.message
                            )
                        )
                    )
                }
            })
    }

    fun getGroups(resultCallback: (CustomResult<List<GroupDetails>>) -> Unit) {
        userPreferences.id?.let {
            CoroutineScope(IO).launch {
                val groupList = mutableListOf<GroupDetails>()
                db.child("users").child(it).child("my_groups").addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            CommonUtilities.hideProgressWheel()
                            for (ids in dataSnapshot.children) {
                                val id = ids.getValue(String::class.java) ?: ""
                                db.child("groups").child(id).child("group_details").get()
                                    .addOnCompleteListener {
                                        val dataSnapshotIn=it.result
                                        if (dataSnapshotIn.value != null) {
                                            val group =
                                                dataSnapshotIn.getValue(GroupDetails::class.java)
                                                    ?: GroupDetails("", "", "", "", 0)

                                            groupList.add(group)


                                            if (ids.key.equals(dataSnapshot.children.last().key))
                                                resultCallback.invoke(CustomResult.Success(groupList))

                                        }
                                    }


                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            resultCallback.invoke(
                                CustomResult.Error(
                                    exception = LocalisedException(
                                        databaseError.message
                                    )
                                )
                            )
                            //handle databaseError
                        }
                    })
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