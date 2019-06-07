package com.app.littlechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.pojo.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_find_friends.*
import java.util.ArrayList

class FindFriends : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    internal var usersList = ArrayList<User>()

    lateinit var adapter : UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friends)

        init()
    }

    private fun init() {


        adapter= UsersAdapter()

        CommonUtilities.setLayoutManager(rvUsers, LinearLayoutManager(this))




        database = FirebaseDatabase.getInstance().getReference("users")
        CommonUtilities.showProgressWheel(this)
        var quaryData = database.orderByChild("name").startAt("Sh").endAt("Sh" + "\uf8ff")


        quaryData .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                CommonUtilities.hideProgressWheel()
                if (dataSnapshot.getValue() != null) {


                    try {
                        for (user in dataSnapshot.children) {

                            usersList.add(user.getValue(User::class.java)?: User("","",""))
                        }
                        adapter.setData(this@FindFriends, usersList)

                        rvUsers.adapter=adapter
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

    }
}
