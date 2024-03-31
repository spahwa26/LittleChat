package com.app.littlechat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.databinding.ActivityFindFriendsBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class FindFriends : AppCompatActivity(), AppInterface {

    private var binding: ActivityFindFriendsBinding? = null

    private lateinit var database: DatabaseReference

    internal var usersList = ArrayList<User>()

    lateinit var adapter: UsersAdapter

    var timer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindFriendsBinding.inflate(layoutInflater)

        init()

        listeners()
        setContentView(binding?.root)
    }

    private fun listeners() {
        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.e("afterTextChanged", "")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.e("beforeTextChanged", "")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                cancelTimer()

                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        searchFriends(p0.toString())
                    }

                }, 600)
            }

        })


        binding?.ivBack?.setOnClickListener { finish() }
    }

    private fun init() {
        binding?.run {

            adapter = UsersAdapter()
            adapter.setData(this@FindFriends, usersList, this@FindFriends)
            rvUsers.adapter = adapter

            CommonUtilities.setLayoutManager(rvUsers, LinearLayoutManager(getActivity()))

            database = FirebaseDatabase.getInstance().getReference("users")
        }


    }

    private fun searchFriends(name: String) {

        if (name.isEmpty()) {
            usersList.clear()
            runOnUiThread { adapter.notifyDataSetChanged() }
            return
        }

        var quaryData = database.orderByChild("name").startAt(name).endAt(name + "\uf8ff")

        quaryData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                if (dataSnapshot.getValue() != null) {
                    try {
                        for (user in dataSnapshot.children) {
                            if (!user.key.equals(FirebaseAuth.getInstance().currentUser?.uid))
                                usersList.add(
                                    user.getValue(User::class.java)
                                        ?: User("", "", "", "", "", "")
                                )
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("", "onCancelled: ")
            }
        })
    }

    private fun cancelTimer() {
        if (timer != null)
            timer?.cancel()
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {
        startActivity(
            Intent(this@FindFriends, Profile::class.java).putExtra(
                "data",
                usersList.get(pos)
            )
        )
    }
}
