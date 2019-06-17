package com.app.littlechat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.adapter.GroupsAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.GroupDetails
import com.app.littlechat.utility.CommonUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_groups.*
import java.util.ArrayList

class Groups : AppCompatActivity(), AppInterface {


    lateinit var activity: Activity

    internal var groupList = ArrayList<GroupDetails>()

    lateinit var adapter: GroupsAdapter

    private var userID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        init()
    }


    private fun init() {

        activity = this
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = GroupsAdapter()
        adapter.setData(this@Groups, groupList, this)
        rvGroups.adapter = adapter

        CommonUtilities.setLayoutManager(rvGroups, LinearLayoutManager(this))

        getGroups()

    }

    private fun getGroups() {
        CommonUtilities.showProgressWheel(activity)
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(userID).child("my_groups")
        ref.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CommonUtilities.hideProgressWheel()
                        groupList.clear()
                        for (ids in dataSnapshot.children) {
                            val id = ids.getValue(String::class.java) ?: ""
                            FirebaseDatabase.getInstance().reference.child("groups").child(id).child("group_details")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {

                                        override fun onDataChange(dataSnapshotIn: DataSnapshot) {
                                            if (dataSnapshotIn.getValue() != null) {
                                                val group = dataSnapshotIn.getValue(GroupDetails::class.java)
                                                        ?: GroupDetails("","", "", "", 0)

                                                groupList.add(group)


                                                if (ids.key.equals(dataSnapshot.children.last().key))
                                                    adapter.notifyDataSetChanged()

                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }
                                    })
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        CommonUtilities.hideProgressWheel()
                        //handle databaseError
                    }
                })
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {
        startActivity(Intent(this@Groups, GroupChat::class.java).putExtra("data", groupList[pos]))

    }
}
