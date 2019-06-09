package com.app.littlechat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.RequestsAdapter
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.Friends
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home_screen.*
import java.util.ArrayList

class HomeScreen : AppCompatActivity(), AppInterface {

    lateinit var activity: Activity

    internal var requestList = ArrayList<Friends>()

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
        adapter.setData(this@HomeScreen, requestList, this)
        rvFriends.adapter = adapter

        CommonUtilities.setLayoutManager(rvFriends, LinearLayoutManager(this))

        database = FirebaseDatabase.getInstance().getReference("users")
        getRequests()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId)
        {
            R.id.find_friends -> startActivity(Intent(this, FindFriends::class.java))

            R.id.profile -> startActivity(Intent(this, Profile::class.java))

            R.id.friend_requests -> startActivity(Intent(this, FriendRequests::class.java))
        }

        return super.onOptionsItemSelected(item)
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {


    }
}
