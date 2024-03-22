package com.app.littlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.app.littlechat.adapter.PageAdapter
import com.app.littlechat.databinding.ActivityHomeScreenBinding
import com.app.littlechat.fragments.FriendsFragment
import com.app.littlechat.fragments.GroupsFragment
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.User
import com.google.android.material.tabs.TabLayout

class HomeScreen : AppCompatActivity(), AppInterface {

    lateinit var binding: ActivityHomeScreenBinding

    lateinit var activity: Activity

    internal var friendList = ArrayList<User>()

    private var userID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)

        init()
        setContentView(binding.root)
    }


    private fun init() {

        binding.run {
            val adapter = PageAdapter(supportFragmentManager)

            //adapter.addFragment(ChatFragment(), "Chats")

            adapter.addFragment(FriendsFragment(), "Friends")

            adapter.addFragment(GroupsFragment(), "Groups")

            chatPager.adapter = adapter

            chatTabs.setupWithViewPager(chatPager)


            chatTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                }

                override fun onTabSelected(p0: TabLayout.Tab?) {

                }
            })

            chatPager.offscreenPageLimit = 2

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.profile -> startActivity(Intent(this, Profile::class.java))

            R.id.friend_requests -> startActivity(Intent(this, FriendRequests::class.java))

        }

        return super.onOptionsItemSelected(item)
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {
        startActivity(
            Intent(activity, ChatScreen::class.java).putExtra(
                "data",
                friendList.get(pos)
            )
        )
    }
}
