package com.app.littlechat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.adapter.ChatAdapter
import com.app.littlechat.adapter.GroupChatAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.Chat
import com.app.littlechat.pojo.GroupDetails
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_chat.*
import java.util.ArrayList

class GroupChat : AppCompatActivity(), AppInterface {

    lateinit var activity: Activity

    internal var chatList = ArrayList<Chat>()

    internal var participantsList = ArrayList<User>()

    lateinit var adapter: GroupChatAdapter

    lateinit var groupDetails: GroupDetails

    private var userID: String = ""

    private var chatID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        init()

        listeners()

    }

    private fun init() {
        activity = this
        groupDetails = intent.getParcelableExtra("data")

        setUiData()

        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = GroupChatAdapter()
        adapter.setData(this@GroupChat, chatList, participantsList, userID, this)
        rvChat.adapter = adapter
        CommonUtilities.setLayoutManager(rvChat, LinearLayoutManager(this, RecyclerView.VERTICAL, false))

        if (groupDetails.id > userID)
            chatID = groupDetails.id + "__" + userID
        else
            chatID = userID + "__" + groupDetails.id


        getParticipantsData(true)
    }

    private fun setUiData() {


        tvName.text = groupDetails.name

        if (!groupDetails.image.isEmpty())
            Picasso.get().load(groupDetails.image).placeholder(R.mipmap.ic_launcher).into(ivImage)
    }

    private fun getParticipantsData(getChats: Boolean) {


        val ref = FirebaseDatabase.getInstance().reference.child("groups").child(groupDetails.id).child("participants")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                CommonUtilities.hideProgressWheel()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        participantsList.clear()
                        for (ids in dataSnapshot.children) {
                            val id = ids.getValue(String::class.java) ?: ""
                            FirebaseDatabase.getInstance().reference.child("users").child(id)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {

                                        override fun onDataChange(dataSnapshotIn: DataSnapshot) {
                                            val user = dataSnapshotIn.getValue(User::class.java)
                                                    ?: User("", "", "", "", "", "")
                                            participantsList.add(user)

                                            if (ids.key.equals(dataSnapshot.children.last().key)) {
                                                adapter.updateParticipantList(participantsList)
                                                if (getChats)
                                                    getChats()
                                                else
                                                    CommonUtilities.hideProgressWheel()
                                            }


                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                            CommonUtilities.hideProgressWheel()
                                        }
                                    })
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            updateGroupDetails()
        }
    }

    private fun updateGroupDetails() {
        CommonUtilities.showProgressWheel(activity)
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupDetails.id).child("group_details")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            try {
                                groupDetails = dataSnapshot.getValue(GroupDetails::class.java)
                                        ?: GroupDetails("", "", "", "", 0)
                                setUiData()
                                getParticipantsData(false)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("onCancelled", "onCancelled: ")
                        CommonUtilities.hideProgressWheel()
                    }
                })
    }

    private fun getChats() {
        val ref = FirebaseDatabase.getInstance().reference.child("groups").child(groupDetails.id).child("messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        chatList.add(dataSnapshot.getValue(Chat::class.java)
                                ?: Chat("", "", "", "", "", 0, "")
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                adapter.notifyDataSetChanged()

                rvChat.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun sendMessage(chat: Chat) {
        FirebaseDatabase.getInstance().reference.child("groups")?.child(groupDetails.id)?.child("messages").push().setValue(chat)
    }

    private fun removeLastMessage() {
        chatList.removeAt(chatList.size - 1)
        adapter.notifyDataSetChanged()
    }


    private fun listeners() {

        btnSend.setOnClickListener {
            if (etMessage.text.toString().trim().isEmpty()) {
                CommonUtilities.showToast(activity, "Please type any message.")
                return@setOnClickListener
            }
            if (!CommonUtilities.isNetworkConnected(activity)) {
                CommonUtilities.showToast(activity, "Please connect to internet first.")
                return@setOnClickListener
            }
            val chat = Chat(userID,
                    groupDetails.id,
                    CommonUtilities.getString(activity, Constants.IMAGE),
                    CommonUtilities.getString(activity, Constants.NAME),
                    etMessage.text.toString(),
                    System.currentTimeMillis(), "sent")
            sendMessage(chat)
            etMessage.setText("")
        }

        tvEdit.setOnClickListener {
            startActivityForResult(Intent(activity, CreateGroup::class.java).putExtra("data", groupDetails).putExtra("participant_list", participantsList), 101)
        }


        ivBack.setOnClickListener { finish() }
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {

    }
}
