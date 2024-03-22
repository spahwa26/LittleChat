package com.app.littlechat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.adapter.ChatAdapter
import com.app.littlechat.databinding.ActivityChatScreenBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.Chat
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ChatScreen : AppCompatActivity(), AppInterface {

    lateinit var binding :  ActivityChatScreenBinding

    internal var chatList = ArrayList<Chat>()

    lateinit var adapter: ChatAdapter

    lateinit var otherUser: User

    private var userID: String = ""

    private var chatID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatScreenBinding.inflate(layoutInflater)

        init()

        listeners()

        setContentView(binding.root)

    }

    private fun init() {
        otherUser = intent.getParcelableExtra("data")!!

        binding.apply {

            tvName.text = otherUser.name

            if (!otherUser.image.isEmpty())
                Picasso.get().load(otherUser.image).placeholder(R.mipmap.ic_launcher).into(ivImage)

            userID = FirebaseAuth.getInstance().getCurrentUser()?.uid ?: ""
            adapter = ChatAdapter()
            adapter.setData(getActivity(), chatList, userID, CommonUtilities.getString(getActivity(), Constants.IMAGE), otherUser.image, this@ChatScreen)
            rvChat.adapter = adapter
            CommonUtilities.setLayoutManager(rvChat, LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false))

            if (otherUser.id > userID)
                chatID = otherUser.id + "__" + userID
            else
                chatID = userID + "__" + otherUser.id


            getChats()
        }
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

                binding.rvChat.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun sendMessage(chat: Chat) {
        FirebaseDatabase.getInstance().reference.child(Constants.CHATS)?.child("$chatID/messages")?.push()?.setValue(chat)
    }

    private fun removeLastMessage() {
        chatList.removeAt(chatList.size - 1)
        adapter.notifyDataSetChanged()
    }


    private fun listeners() {

        binding.apply {

            btnSend.setOnClickListener {
                if (etMessage.text.toString().trim().isEmpty()) {
                    CommonUtilities.showToast(getActivity(), "Please type any message.")
                    return@setOnClickListener
                }
                if (!CommonUtilities.isNetworkConnected(getActivity())) {
                    CommonUtilities.showToast(getActivity(), "Please connect to internet first.")
                    return@setOnClickListener
                }
                val chat = Chat(userID,
                    otherUser.id,
                    CommonUtilities.getString(getActivity(), Constants.IMAGE),
                    CommonUtilities.getString(getActivity(), Constants.NAME),
                    etMessage.text.toString(),
                    System.currentTimeMillis(), "sent")
                sendMessage(chat)
                etMessage.setText("")
            }

            ivBack.setOnClickListener { finish() }
        }
    }


    override fun handleEvent(pos: Int, act: Int, map: Map<String, Any>?) {

    }
}
