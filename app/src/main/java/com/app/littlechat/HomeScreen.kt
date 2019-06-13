package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home_screen.*
import java.util.*

class HomeScreen : AppCompatActivity(), AppInterface {

    lateinit var activity: Activity

    internal var friendList = ArrayList<User>()

    lateinit var adapter: UsersAdapter

    private var userID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        init()
    }


    private fun init() {

        activity = this
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = UsersAdapter()
        adapter.setData(this@HomeScreen, friendList, this)
        rvFriends.adapter = adapter

        CommonUtilities.setLayoutManager(rvFriends, LinearLayoutManager(this))

        getFriends()

    }

    private fun getFriends() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS).child(userID)
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    CommonUtilities.hideProgressWheel()
                    friendList.clear()
                    if (dataSnapshot.getValue() != null) {
                        try {
                            for (user in dataSnapshot.children) {
                                if (user.key.equals(dataSnapshot.children.last().key))
                                    getUsersData(user.key ?: "",true)
                                else
                                    getUsersData(user.key ?: "",false)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CommonUtilities.hideProgressWheel()
                    //handle databaseError
                }
            })
    }

    private fun getUsersData(id: String, notify: Boolean) {
        FirebaseDatabase.getInstance().getReference().child("users/$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            friendList.add(dataSnapshot.getValue(User::class.java) ?: User("", "", "", "", "", ""))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if(notify)
                        adapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("onCancelled", "onCancelled: ")
                }
            })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.find_friends -> startActivity(Intent(this, FindFriends::class.java))

            R.id.profile -> startActivity(Intent(this, Profile::class.java))

            R.id.friend_requests -> startActivity(Intent(this, FriendRequests::class.java))
        }

        return super.onOptionsItemSelected(item)
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {


    }
}
