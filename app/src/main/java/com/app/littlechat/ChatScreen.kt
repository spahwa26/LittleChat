package com.app.littlechat

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.adapter.ChatAdapter
import com.app.littlechat.adapter.UsersAdapter
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.Chat
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_screen.*
import java.util.ArrayList

class ChatScreen : AppCompatActivity(), AppInterface {

    lateinit var activity: Activity

    internal var chatList = ArrayList<Chat>()

    lateinit var adapter: ChatAdapter

    lateinit var otherUser: User

    private var userID: String = ""

    private var chatID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        init()

        listeners()

    }

    private fun init() {
        activity = this
        otherUser = intent.getParcelableExtra("data")

        tvName.text = otherUser.name

        if (!otherUser.image.isEmpty())
            Picasso.get().load(otherUser.image).placeholder(R.mipmap.ic_launcher).into(ivImage)

        userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
        adapter = ChatAdapter()
        adapter.setData(this@ChatScreen, chatList, userID, CommonUtilities.getString(activity, Constants.IMAGE), otherUser.image, this)
        rvChat.adapter = adapter
        CommonUtilities.setLayoutManager(rvChat, LinearLayoutManager(this, RecyclerView.VERTICAL, false))

        if (otherUser.id > userID)
            chatID = otherUser.id + "__" + userID
        else
            chatID = userID + "__" + otherUser.id


        getChats()
    }

    private fun getChats() {
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.CHATS).child(chatID).child("messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        chatList.add(dataSnapshot.getValue(Chat::class.java)
                                ?: Chat("", "", "", "", "", 0, ""))

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                adapter.notifyDataSetChanged()

                rvChat.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun sendMessage(chat: Chat) {
        FirebaseDatabase.getInstance().reference.child(Constants.CHATS)?.child("$chatID/messages")?.push().setValue(chat)
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
                otherUser.id,
                CommonUtilities.getString(activity, Constants.IMAGE),
                CommonUtilities.getString(activity, Constants.NAME),
                etMessage.text.toString(),
                System.currentTimeMillis(), "sent")
            sendMessage(chat)
            etMessage.setText("")
        }

        ivBack.setOnClickListener { finish() }
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {

    }
}
