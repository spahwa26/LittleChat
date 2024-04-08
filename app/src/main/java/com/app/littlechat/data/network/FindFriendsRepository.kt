package com.app.littlechat.data.network

import android.util.Log
import androidx.compose.ui.text.toUpperCase
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.Constants.Companion.EMPTY_LIST
import com.app.littlechat.utility.Constants.Companion.NAME
import com.app.littlechat.utility.Constants.Companion.USERS
import com.app.littlechat.utility.LocalisedException
import com.app.littlechat.utility.setError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FindFriendsRepository @Inject constructor() {
    private val db by lazy {
        FirebaseDatabase.getInstance().getReference(USERS)
    }


    fun searchFriends(name: String, resultCallback: (CustomResult<List<User>>) -> Unit) {

        val queryData =FirebaseDatabase.getInstance().getReference(USERS).orderByChild(NAME).startAt(name.uppercase()).endAt(name.lowercase() + "\uf8ff")

        //val queryData = db.orderByChild(NAME).startAt(name).endAt(name + "\uf8ff")

        queryData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usersList = mutableListOf<User>()
                if (dataSnapshot.value != null) {
                    try {
                        for (user in dataSnapshot.children) {
                            if (!user.key.equals(FirebaseAuth.getInstance().currentUser?.uid))
                                user.getValue(User::class.java)?.let {
                                    usersList.add(it)
                                }
                        }

                    } catch (e: Exception) {
                        setError(resultCallback, e.message)
                    }

                }

                resultCallback.invoke(CustomResult.Success(usersList))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("", "onCancelled: ")//todo: handle
            }
        })
    }

}