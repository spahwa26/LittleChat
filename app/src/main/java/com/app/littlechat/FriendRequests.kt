package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.RequestsAdapter
import com.app.littlechat.databinding.ActivityFriendRequestsBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class FriendRequests : AppCompatActivity(), AppInterface {

    private var binding : ActivityFriendRequestsBinding?=null

    lateinit var activity: Activity
    private lateinit var database: DatabaseReference

    internal var requestList = ArrayList<User>()

    lateinit var adapter: RequestsAdapter

    private var userID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFriendRequestsBinding.inflate(layoutInflater)

        init()


        binding?.ivBack?.setOnClickListener { finish() }
        setContentView(binding?.root)
    }

    private fun init() {

        activity = this
        binding?.run {
            userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
            adapter = RequestsAdapter()
            adapter.setData(this@FriendRequests, requestList, this@FriendRequests)
            rvRequests.adapter = adapter
            CommonUtilities.setLayoutManager(rvRequests, LinearLayoutManager(this@FriendRequests))
            database = FirebaseDatabase.getInstance().getReference("users")
            getRequests()
        }

    }


    private fun getRequests() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS).child(userID)
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    requestList.clear()
                    if (dataSnapshot.getValue() != null) {
                        try {
                            for (user in dataSnapshot.children) {
                                val savedUser = user.getValue(User::class.java) ?: User("", "", "", "", "", "")
                                if (!user.key.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                                    if (user.key.equals(dataSnapshot.children.last().key))
                                        getUsersData(user.key ?: "", true, savedUser.status)
                                    else
                                        getUsersData(user.key ?: "", false, savedUser.status)
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    else{
                        adapter.notifyDataSetChanged()
                    }

                    CommonUtilities.hideProgressWheel()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CommonUtilities.hideProgressWheel()
                    //handle databaseError
                }
            })
    }


    private fun getUsersData(id: String, notify: Boolean, status : String) {
        FirebaseDatabase.getInstance().getReference().child("users/$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            val user = dataSnapshot.getValue(User::class.java) ?: User("", "", "", "", "", "")
                            user.status = status
                            requestList.add(user)
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

    private fun acceptRequest(pos: Int) {
        CommonUtilities.showProgressWheel(activity)
        val user = requestList.get(pos)
        FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(userID)?.child(user.id)?.setValue(
            User(user.id, user.name, user.email, user.phone_number, user.image, Constants.ACCEPTED)
        )?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(user.id)?.child(userID)?.setValue(CommonUtilities.getUserData(activity))
                CommonUtilities.showToast(activity, "Request Accepted")
                cancelRequest(pos)
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false, true)
        }
    }

    private fun cancelRequest(pos: Int) {
        CommonUtilities.showProgressWheel(activity)
        val id = requestList.get(pos).id
        FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS)?.child(userID)?.child(id)
            ?.removeValue()?.addOnCompleteListener { task ->
            CommonUtilities.hideProgressWheel()
            if (task.isSuccessful) {
                FirebaseDatabase.getInstance().reference.child(Constants.REQUESTS)?.child(id)?.child(userID)?.removeValue()
            } else
                CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
        }?.addOnFailureListener { e ->
            CommonUtilities.hideProgressWheel()
            CommonUtilities.showAlert(activity, e.message, false, true)
        }
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {

        when (act) {
            0 ->  startActivity(Intent(this@FriendRequests, Profile::class.java).putExtra("data", requestList.get(pos)))

            -1 -> acceptRequest(pos)

            -3 -> cancelRequest(pos)
        }

    }
}
