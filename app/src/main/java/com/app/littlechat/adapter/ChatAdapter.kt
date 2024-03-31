package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.databinding.LayoutChatBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.data.model.Chat
import com.squareup.picasso.Picasso


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    private lateinit var list: ArrayList<Chat>
    private lateinit var context: Context
    private lateinit var appInterface: AppInterface
    private lateinit var myId: String
    private lateinit var myPic: String
    private lateinit var othersPic: String

    fun setData(
        context: Context,
        list: ArrayList<Chat>,
        myId: String,
        myPic: String,
        othersPic: String,
        appInterface: AppInterface
    ) {
        this.list = list
        this.context = context
        this.myId = myId
        this.myPic = myPic
        this.othersPic = othersPic
        this.appInterface = appInterface
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val binding = LayoutChatBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        val holder = MyViewHolder(binding.root)
        holder.setBinding(binding)
        return holder
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var binding: LayoutChatBinding? = null

        fun setBinding(_binding: LayoutChatBinding) {
            binding = _binding
        }

        fun onBind(position: Int) {
            binding?.apply {
                if (myId.equals(list.get(position).sender_id)) {
                    rlLeft.visibility = GONE
                    rlRight.visibility = VISIBLE
                    tvMyMessage.text = list.get(position).message
                    if (!myPic.isEmpty())
                        Picasso.get().load(myPic).placeholder(R.mipmap.ic_launcher).into(ivMyPic)
                } else {
                    rlLeft.visibility = VISIBLE
                    rlRight.visibility = GONE
                    tvOthersMessage.text = list.get(position).message
                    if (!othersPic.isEmpty())
                        Picasso.get().load(othersPic).placeholder(R.mipmap.ic_launcher)
                            .into(ivOthersPic)
                }
            }
        }
    }


}
