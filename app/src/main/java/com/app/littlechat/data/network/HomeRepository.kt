package com.app.littlechat.data.network

import android.net.Uri
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.GroupDetails
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.Constants.Companion.GROUPS
import com.app.littlechat.utility.Constants.Companion.GROUP_DETAILS
import com.app.littlechat.utility.Constants.Companion.IMAGE
import com.app.littlechat.utility.Constants.Companion.MY_GROUPS
import com.app.littlechat.utility.Constants.Companion.NAME
import com.app.littlechat.utility.Constants.Companion.PARTICIPANTS
import com.app.littlechat.utility.Constants.Companion.USERS
import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.SomethingWentWrongException
import com.app.littlechat.utility.deleteImageFile
import com.app.littlechat.utility.getImageFile
import com.app.littlechat.utility.setError
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

class HomeRepository @Inject constructor(private val userPreferences: UserPreferences) {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }
    private var friendsRef: DatabaseReference? = null
    private var friendsListener: ValueEventListener? = null
    private var groupsRef: DatabaseReference? = null
    private var groupsListener: ValueEventListener? = null

    fun getGroupImageName(groupId: String) = "${groupId}_${Constants.GROUP_ICON}"

    fun getFriends(resultCallback: (CustomResult<List<User>>) -> Unit) {
        userPreferences.id?.let {
            friendsRef = db.child(Constants.FRIENDS).child(it)
            friendsListener = friendsRef?.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val friendList = mutableListOf<User>()
                        if (dataSnapshot.value != null) {
                            try {
                                for (user in dataSnapshot.children) {
                                    user.getValue(User::class.java)?.let { friend ->
                                        if (user.key.equals(dataSnapshot.children.last().key))
                                            getUsersData(user.key ?: "", true, friend.status, friendList, resultCallback)
                                        else
                                            getUsersData(user.key ?: "", false, friend.status, friendList, resultCallback)
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
        db.child(USERS).child(id)
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
        userPreferences.id?.let { myId ->
            groupsRef = db.child(USERS).child(myId).child(MY_GROUPS)
            groupsListener = groupsRef?.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val groupList = mutableListOf<GroupDetails>()
                        for (ids in dataSnapshot.children) {
                            val id = ids.getValue(String::class.java) ?: ""
                            db.child(GROUPS).child(id).child(GROUP_DETAILS).get()
                                .addOnCompleteListener {
                                    val dataSnapshotIn = it.result
                                    if (dataSnapshotIn.value != null) {
                                        val group =
                                            dataSnapshotIn.getValue(GroupDetails::class.java)
                                        group?.let {
                                            groupList.add(group)
                                        }
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

    fun createGroup(
        participantsList: MutableList<String>,
        groupName: String,
        groupID: String,
        isUploadImage: Boolean,
        isEdit: Boolean = false,
        groupDetails: GroupDetails? = null,
        resultCallback: (CustomResult<Unit>) -> Unit
    ) {
        if (isUploadImage) {
            uploadImage(groupID) { imgResult ->
                when (imgResult) {
                    is CustomResult.Success -> {
                        continueCreateGroup(
                            participantsList,
                            groupName,
                            groupID,
                            imgResult.data,
                            isEdit,
                            groupDetails,
                            resultCallback
                        )
                    }

                    is CustomResult.Error -> {
                        setError(resultCallback, imgResult.exception.message)
                    }
                }
            }
        } else
            continueCreateGroup(
                participantsList,
                groupName,
                groupID,
                "",
                isEdit,
                groupDetails,
                resultCallback
            )
    }

    private fun continueCreateGroup(
        participantsList: MutableList<String>,
        groupName: String,
        groupID: String,
        imageUrl: String,
        isEdit: Boolean = false,
        groupDetails: GroupDetails? = null,
        resultCallback: (CustomResult<Unit>) -> Unit
    ) {
        val userId = userPreferences.id
        if (userId != null) {
            val setUpdateTask: Task<Void>
            if (isEdit) {
                val grpMap = HashMap<String, Any>()
                groupDetails?.let {
                    grpMap[NAME] = groupName
                    grpMap[IMAGE] = imageUrl.ifBlank { groupDetails.image }
                }
                setUpdateTask =
                    db.child(GROUPS).child(groupID).child(GROUP_DETAILS).updateChildren(grpMap)
            } else {
                val details = GroupDetails(
                    groupID,
                    groupName,
                    imageUrl,
                    userId,
                    System.currentTimeMillis()
                )
                setUpdateTask =
                    db.child(GROUPS).child(groupID).child(GROUP_DETAILS).setValue(details)
            }
            setUpdateTask.addOnCompleteListener { taskComp ->
                if (taskComp.isSuccessful) {
                    db.child(GROUPS).child(groupID).child(PARTICIPANTS).setValue(participantsList)
                        .addOnCompleteListener { updateMembersTask ->
                            if (updateMembersTask.isSuccessful) {
                                participantsList.add(userId)
                                for (member in participantsList) {
                                    FirebaseDatabase.getInstance().reference.child(USERS)
                                        .child(member).child(MY_GROUPS).get()
                                        .addOnCompleteListener { taskGIds ->
                                            if (taskGIds.isSuccessful) {
                                                val list: List<String> =
                                                    taskGIds.result.getValue(listOf<String>()::class.java)
                                                        ?: emptyList()
                                                if (!list.contains(member)) {
                                                    db.child(USERS).child(member).child(MY_GROUPS)
                                                        .push().setValue(groupID)
                                                }
                                            }
                                        }
                                    if (member == participantsList.last()) {
                                        resultCallback.invoke(CustomResult.Success(Unit))
                                    }
                                }
                            } else {
                                setError(resultCallback, updateMembersTask.exception?.message)
                            }
                        }
                } else {
                    setError(resultCallback, taskComp.exception?.message)
                }
            }.addOnFailureListener { e ->
                setError(resultCallback, e.message)
            }
        }
    }


    private fun uploadImage(groupID: String, resultCallback: (CustomResult<String>) -> Unit) {
        val file = Uri.fromFile(userPreferences.context.getImageFile(getGroupImageName(groupID)))
        val storageRef = FirebaseStorage.getInstance().reference
        val riversRef = storageRef.child(Constants.FIREBASE_STORAGE_PATH + file.lastPathSegment)
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
                userPreferences.context.deleteImageFile(getGroupImageName(groupID))
                resultCallback.invoke(CustomResult.Success(task.result.toString()))
            } else {
                setError(resultCallback, task.exception?.message)
            }
        }
    }


    fun removeListeners() {
        friendsListener?.let {
            friendsRef?.removeEventListener(it)
            friendsListener = null
        }
        groupsListener?.let {
            groupsRef?.removeEventListener(it)
            groupsListener = null
        }
    }

}