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
import com.app.littlechat.FindFriends
import com.app.littlechat.GroupChat
import com.app.littlechat.R
import com.app.littlechat.adapter.GroupsAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.GroupDetails
import com.app.littlechat.utility.CommonUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_groups2.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupsFragment : Fragment(), AppInterface {


    lateinit var activity: Activity

    internal var groupList = ArrayList<GroupDetails>()

    lateinit var adapter: GroupsAdapter

    private var userID: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        listeners()
    }

    private fun listeners() {
        fabCreateGroup.setOnClickListener { startActivity(Intent(activity, CreateGroup::class.java)) }

    }

    private fun init() {

        activity = getActivity() as Activity
        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = GroupsAdapter()
        adapter.setData(activity, groupList, this)
        rvGroups.adapter = adapter

        CommonUtilities.setLayoutManager(rvGroups, LinearLayoutManager(activity))

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
