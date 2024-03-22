package com.app.littlechat.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.littlechat.CreateGroup
import com.app.littlechat.GroupChat
import com.app.littlechat.adapter.GroupsAdapter
import com.app.littlechat.databinding.FragmentGroups2Binding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.GroupDetails
import com.app.littlechat.utility.CommonUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GroupsFragment : Fragment(), AppInterface {

    private lateinit var binding: FragmentGroups2Binding

    lateinit var activity: Activity

    internal var groupList = ArrayList<GroupDetails>()

    lateinit var adapter: GroupsAdapter

    private var userID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroups2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        listeners()
    }

    private fun listeners() {
        binding.fabCreateGroup.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    CreateGroup::class.java
                )
            )
        }

    }

    private fun init() {

        activity = getActivity() as Activity
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = GroupsAdapter()
        adapter.setData(activity, groupList, this)
        binding.run {
            rvGroups.adapter = adapter

            CommonUtilities.setLayoutManager(rvGroups, LinearLayoutManager(activity))
        }

        getGroups()

    }

    private fun getGroups() {
        CommonUtilities.showProgressWheel(activity)
        val ref =
            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("my_groups")
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    CommonUtilities.hideProgressWheel()
                    groupList.clear()
                    for (ids in dataSnapshot.children) {
                        val id = ids.getValue(String::class.java) ?: ""
                        FirebaseDatabase.getInstance().reference.child("groups").child(id)
                            .child("group_details")
                            .addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onDataChange(dataSnapshotIn: DataSnapshot) {
                                    if (dataSnapshotIn.getValue() != null) {
                                        val group =
                                            dataSnapshotIn.getValue(GroupDetails::class.java)
                                                ?: GroupDetails("", "", "", "", 0)

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
        startActivity(Intent(activity, GroupChat::class.java).putExtra("data", groupList[pos]))

    }

}
