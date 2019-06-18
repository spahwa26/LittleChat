package com.app.littlechat.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.ChatScreen
import com.app.littlechat.CreateGroup
import com.app.littlechat.FindFriends

import com.app.littlechat.R
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
import kotlinx.android.synthetic.main.fragment_friends.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FriendsFragment : Fragment(), AppInterface {

    lateinit var activity: Activity

    internal var friendList = ArrayList<User>()

    lateinit var adapter: UsersAdapter

    private var userID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return  inflater.inflate(R.layout.fragment_friends, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


        listeners()
    }

    private fun listeners() {
        fabFindFriends.setOnClickListener { startActivity(Intent(activity, FindFriends::class.java)) }
    }

    private fun init() {

        activity = getActivity() as Activity
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = UsersAdapter()
        adapter.setData(activity, friendList, this)
        rvFriends.adapter = adapter

        CommonUtilities.setLayoutManager(rvFriends, LinearLayoutManager(activity))

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
                                    val savedUser = user.getValue(User::class.java) ?: User("", "", "", "", "", "")
                                    if (user.key.equals(dataSnapshot.children.last().key))
                                        getUsersData(user.key ?: "",true, savedUser.status)
                                    else
                                        getUsersData(user.key ?: "",false, savedUser.status)
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        else{
                            adapter.notifyDataSetChanged()
                        }

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
                                friendList.add(user)
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





    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {
        startActivity(Intent(activity, ChatScreen::class.java).putExtra("data", friendList.get(pos)))
    }

}
