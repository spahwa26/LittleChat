package com.app.littlechat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_find_friends.*
import java.util.*

class FindFriends : AppCompatActivity(), AppInterface {

    private lateinit var database: DatabaseReference

    internal var usersList = ArrayList<User>()

    lateinit var adapter: UsersAdapter

    var timer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friends)

        init()

        listeners()
    }

    private fun listeners() {
        etSearch.addTextChangedListener(object : TextWatcher {
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
    }

    private fun init() {


        adapter = UsersAdapter()
        adapter.setData(this@FindFriends, usersList, this)
        rvUsers.adapter = adapter

        CommonUtilities.setLayoutManager(rvUsers, LinearLayoutManager(this))

        database = FirebaseDatabase.getInstance().getReference("users")

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
                                usersList.add(user.getValue(User::class.java)
                                        ?: User("", "", "", "", "",""))
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
        startActivity(Intent(this@FindFriends, Profile::class.java).putExtra("data", usersList.get(pos)))
    }
}
