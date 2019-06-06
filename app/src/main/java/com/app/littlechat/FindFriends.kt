package com.app.littlechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*

class FindFriends : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friends)

        init()
    }

    private fun init() {
        database = FirebaseDatabase.getInstance().getReference("users")

        var quaryData = database.orderByChild("name").startAt("Sh").endAt("Sh" + "\uf8ff")


        quaryData .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                CommonUtilities.hideProgressWheel()
                if (dataSnapshot.getValue() != null) {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("", "onCancelled: ")
                CommonUtilities.hideProgressWheel()
            }
        })

        Log.e("data", quaryData.toString())
    }
}
