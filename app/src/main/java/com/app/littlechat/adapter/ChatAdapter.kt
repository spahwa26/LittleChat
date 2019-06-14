package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.Chat
import com.app.littlechat.pojo.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_chat.view.*


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<Chat>
    lateinit var context: Context
    lateinit var appInterface: AppInterface
    lateinit var myId: String
    lateinit var myPic: String
    lateinit var othersPic: String

    fun setData(context: Context, list: ArrayList<Chat>, myId: String, myPic: String, othersPic: String, appInterface: AppInterface) {
        this.list = list
        this.context = context
        this.myId = myId
        this.myPic = myPic
        this.othersPic = othersPic
        this.appInterface = appInterface
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.layout_chat, p0, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (myId.equals(list.get(position).id)) {
            holder.rl_left.visibility = GONE
            holder.rl_right.visibility = VISIBLE
            holder.tvMyMessage.text = list.get(position).message
            if (!myPic.isEmpty())
                Picasso.get().load(myPic).placeholder(R.mipmap.ic_launcher).into(holder.ivMyPic)
        } else {
            holder.rl_left.visibility = VISIBLE
            holder.rl_right.visibility = GONE
            holder.tvOthersMessage.text = list.get(position).message
            if (!othersPic.isEmpty())
                Picasso.get().load(othersPic).placeholder(R.mipmap.ic_launcher).into(holder.ivOthersPic)
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOthersMessage = view.tvOthersMessage
        val ivOthersPic = view.ivOthersPic
        val rl_left = view.rl_left
        val tvMyMessage = view.tvMyMessage
        val ivMyPic = view.ivMyPic
        val rl_right = view.rl_right
    }


}
