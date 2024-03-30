package com.app.littlechat.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.ChatScreen
import com.app.littlechat.FindFriends
import com.app.littlechat.Profile
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.databinding.FragmentFriendsBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsFragment : Fragment(), AppInterface {

    private lateinit var binding: FragmentFriendsBinding

    lateinit var activity: Activity

    internal var friendList = ArrayList<User>()

    lateinit var adapter: UsersAdapter

    private var userID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


        listeners()
    }

    private fun listeners() {
        binding.fabFindFriends.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    FindFriends::class.java
                )
            )
        }
    }

    private fun init() {

        activity = getActivity() as Activity
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = UsersAdapter()
        adapter.setData(activity, friendList, this)
        binding.run {
            rvFriends.adapter = adapter
            CommonUtilities.setLayoutManager(rvFriends, LinearLayoutManager(activity))
        }
        getFriends()

    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {


        val builder = AlertDialog.Builder(activity)

        val animals = arrayOf("Chat", "Show profile", "Remove from list")

        builder.setItems(animals) { dialog, which ->
            when (which) {
                0 -> startActivity(
                    Intent(activity, ChatScreen::class.java).putExtra(
                        "data",
                        friendList.get(pos)
                    )
                )

                1 -> startActivity(
                    Intent(activity, Profile::class.java).putExtra(
                        "data",
                        friendList.get(pos)
                    )
                )

                2 -> removeFriend(pos)
            }
        }

        val dialog = builder.create()
        dialog.show()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////Firebase////////////////////////////////////////////

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
                                val savedUser =
                                    user.getValue(User::class.java) ?: User("", "", "", "", "", "")
                                if (user.key.equals(dataSnapshot.children.last().key))
                                    getUsersData(user.key ?: "", true, savedUser.status)
                                else
                                    getUsersData(user.key ?: "", false, savedUser.status)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        adapter.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CommonUtilities.hideProgressWheel()
                    //handle databaseError
                }
            })
    }

    private fun getUsersData(id: String, notify: Boolean, status: String) {
        FirebaseDatabase.getInstance().getReference().child("users/$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            val user = dataSnapshot.getValue(User::class.java) ?: User(
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                            user.status = status
                            friendList.add(user)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (notify)
                        adapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("onCancelled", "onCancelled: ")
                }
            })
    }

    private fun removeFriend(pos: Int) {
        CommonUtilities.showProgressWheel(activity)
        val id = friendList.get(pos).id
        FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(userID)?.child(id)
            ?.removeValue()?.addOnCompleteListener { task ->
                CommonUtilities.hideProgressWheel()
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().reference.child(Constants.FRIENDS)?.child(id)
                        ?.child(userID)?.removeValue()
                } else
                    CommonUtilities.showAlert(activity, task.exception!!.message, false, true)
            }?.addOnFailureListener { e ->
                CommonUtilities.hideProgressWheel()
                CommonUtilities.showAlert(activity, e.message, false, true)
            }
    }


}
