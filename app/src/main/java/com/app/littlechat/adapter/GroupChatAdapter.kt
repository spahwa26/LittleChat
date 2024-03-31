package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.databinding.LayoutChatBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.data.model.Chat
import com.app.littlechat.data.model.User
import com.app.littlechat.utility.CommonUtilities
import com.app.littlechat.utility.Constants
import com.squareup.picasso.Picasso


class GroupChatAdapter : RecyclerView.Adapter<GroupChatAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<Chat>
    internal var participantsList = java.util.ArrayList<User>()
    lateinit var context: Context
    lateinit var appInterface: AppInterface
    lateinit var myId: String

    fun setData(
        context: Context,
        list: ArrayList<Chat>,
        participantsList: ArrayList<User>,
        myId: String,
        appInterface: AppInterface
    ) {
        this.list = list
        this.participantsList = participantsList
        this.context = context
        this.myId = myId
        this.appInterface = appInterface
    }

    fun updateParticipantList(participantsList: ArrayList<User>) {
        this.participantsList = participantsList
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutChatBinding.inflate(LayoutInflater.from(p0.context), p0, false))
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    private fun getParticipantsImage(id: String): String {
        var image = ""
        for (user in participantsList) {
            if (user.id.equals(id))
                image = user.image
        }
        return image
    }

    inner class MyViewHolder(val binding: LayoutChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.run {
                if (myId.equals(list.get(position).sender_id)) {
                    rlLeft.visibility = GONE
                    rlRight.visibility = VISIBLE
                    tvMyMessage.text = list.get(position).message
                    if (!CommonUtilities.getString(context, Constants.IMAGE).isEmpty())
                        Picasso.get().load(CommonUtilities.getString(context, Constants.IMAGE))
                            .placeholder(R.mipmap.ic_launcher).into(ivMyPic)
                } else {
                    rlLeft.visibility = VISIBLE
                    rlRight.visibility = GONE
                    tvOthersMessage.text = list.get(position).message
                    val imageUrl = getParticipantsImage(list.get(position).sender_id)
                    if (!imageUrl.isEmpty())
                        Picasso.get().load(imageUrl).placeholder(R.mipmap.ic_launcher)
                            .into(ivOthersPic)
                }
            }
        }
    }


}
