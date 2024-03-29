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
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_chat.view.*


class GroupChatAdapter : RecyclerView.Adapter<GroupChatAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<Chat>
    internal var participantsList = java.util.ArrayList<User>()
    lateinit var context: Context
    lateinit var appInterface: AppInterface
    lateinit var myId: String

    fun setData(context: Context, list: ArrayList<Chat>,  participantsList: ArrayList<User>, myId: String, appInterface: AppInterface) {
        this.list = list
        this.participantsList = participantsList
        this.context = context
        this.myId = myId
        this.appInterface = appInterface
    }

    fun updateParticipantList(participantsList: ArrayList<User>)
    {
        this.participantsList = participantsList
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.layout_chat, p0, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (myId.equals(list.get(position).sender_id)) {
            holder.rl_left.visibility = GONE
            holder.rl_right.visibility = VISIBLE
            holder.tvMyMessage.text = list.get(position).message
            if (!CommonUtilities.getString(context, Constants.IMAGE).isEmpty())
                Picasso.get().load(CommonUtilities.getString(context, Constants.IMAGE)).placeholder(R.mipmap.ic_launcher).into(holder.ivMyPic)
        } else {
            holder.rl_left.visibility = VISIBLE
            holder.rl_right.visibility = GONE
            holder.tvOthersMessage.text = list.get(position).message
            val imageUrl=getParticipantsImage(list.get(position).sender_id)
            if (!imageUrl.isEmpty())
                Picasso.get().load(imageUrl).placeholder(R.mipmap.ic_launcher).into(holder.ivOthersPic)
        }
    }

    private fun getParticipantsImage(id : String): String {
        var image = ""
        for(user in participantsList)
        {
            if(user.id.equals(id))
                image=user.image
        }
        return image
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
