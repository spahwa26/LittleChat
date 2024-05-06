package com.app.littlechat.data.network

import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.setError
import com.google.firebase.database.FirebaseDatabase

abstract class CommonRepo {

    val db by lazy {
        FirebaseDatabase.getInstance().getReference()
    }
    fun getParticipantsData(
        groupId: String,
        resultCallback: (CustomResult<List<User>>) -> Unit
    ) {

        val participantsList = mutableListOf<User>()
        db.child(Constants.GROUPS).child(groupId)
            .child(Constants.PARTICIPANTS).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    if (dataSnapshot.value != null) {
                        try {
                            participantsList.clear()
                            for (ids in dataSnapshot.children) {
                                ids.getValue(String::class.java)?.let { id ->
                                    db.child(Constants.USERS).child(id).get().addOnCompleteListener { task1 ->
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

}