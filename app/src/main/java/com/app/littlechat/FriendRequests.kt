package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.RequestsAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.Friends
import com.app.littlechat.utility.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friend_requests.*
import java.util.*

class FriendRequests : AppCompatActivity(), AppInterface {

    lateinit var activity: Activity
    private lateinit var database: DatabaseReference

    internal var requestList = ArrayList<Friends>()

    lateinit var adapter: RequestsAdapter

    private var userID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_requests)

        init()
    }

    private fun init() {

        activity = this
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = RequestsAdapter()
        adapter.setData(this@FriendRequests, requestList, this)
        rvRequests.adapter = adapter

        CommonUtilities.setLayoutManager(rvRequests, LinearLayoutManager(this))

        database = FirebaseDatabase.getInstance().getReference("users")
        getRequests()

    }


    private fun getRequests() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS).child(userID)
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    CommonUtilities.hideProgressWheel()

                    requestList.clear()
                    if (dataSnapshot.getValue() != null) {
                        try {
                            for (user in dataSnapshot.children) {
                                if (!user.key.equals(FirebaseAuth.getInstance().currentUser?.uid))
                                    requestList.add(
                                        user.getValue(Friends::class.java)
                                            ?: Friends("", "", "", "", "", "")
                                    )
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CommonUtilities.hideProgressWheel()
                    //handle databaseError
                }
            })
    }

    private fun acceptRequest(pos: Int) {
        CommonUtilities.showProgressWheel(activity)
        val user = requestList.get(pos)
        FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(userID)?.child(user.id)?.setValue(
            Friends(user.id, user.name, user.email, user.phone_number, user.image, Constants.ACCEPTED)
        )?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                CommonUtilities.showToast(activity, "Request Accepted")
                cancelRequest(pos)
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false)
        }
    }

    private fun cancelRequest(pos: Int) {
        CommonUtilities.showProgressWheel(activity)
        FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS)?.child(userID)?.child(requestList.get(pos).id)
            ?.removeValue()?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS)?.child(requestList.get(pos).id)?.child(userID)?.removeValue()
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false)
        }
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {

        when (act) {
            0 -> startActivity(Intent(this, FindFriends::class.java))

            -1 -> acceptRequest(pos)

            -3 -> cancelRequest(pos)
        }

    }
}
